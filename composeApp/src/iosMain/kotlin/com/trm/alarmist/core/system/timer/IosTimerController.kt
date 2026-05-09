package com.trm.alarmist.core.system.timer

import com.trm.alarmist.core.domain.model.TimerState
import com.trm.alarmist.core.system.notification.IosTimerNotifications
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.Foundation.NSUserDefaults
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class IosTimerController(
  private val notifications: IosTimerNotifications,
  private val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  private val _state = MutableStateFlow(loadState())
  val state: StateFlow<TimerState> = _state.asStateFlow()

  private val _duration = MutableStateFlow(loadDuration())
  val duration: StateFlow<Duration> = _duration.asStateFlow()

  private val _initialDuration = MutableStateFlow(loadInitialDuration())
  val initialDuration: StateFlow<Duration> = _initialDuration.asStateFlow()

  private var tickerJob: Job? = null

  private var endEpochMillis: Long? = loadEndEpochMillis()
  private var remainingMillisWhenPaused: Long = loadRemainingMillisWhenPaused()

  fun start(duration: Duration) {
    if (duration.inWholeMilliseconds <= 0L) return
    stopTicker()

    val millis = duration.inWholeMilliseconds
    val now = nowEpochMillis()
    _initialDuration.value = duration
    _duration.value = duration
    _state.value = TimerState.RUNNING

    remainingMillisWhenPaused = millis
    endEpochMillis = now + millis

    persistAll()
    startTicker()
  }

  fun toggleRunning() {
    when (_state.value) {
      TimerState.RUNNING -> pause()
      TimerState.PAUSED -> resume()
      TimerState.ELAPSED -> reset()
      TimerState.IDLE -> Unit
    }
  }

  fun addDuration(delta: Duration) = updateDuration(delta)

  fun subtractDuration(delta: Duration) = updateDuration(-delta)

  fun reset() {
    notifications.cancelElapsedNotification()
    stopTicker()

    val init = _initialDuration.value
    if (init.inWholeMilliseconds <= 0L) return

    _duration.value = init
    remainingMillisWhenPaused = init.inWholeMilliseconds
    endEpochMillis = null
    _state.value = TimerState.PAUSED
    persistAll()
  }

  fun cancel() {
    notifications.cancelElapsedNotification()
    stopTicker()

    _state.value = TimerState.IDLE
    _duration.value = Duration.ZERO
    _initialDuration.value = Duration.ZERO
    endEpochMillis = null
    remainingMillisWhenPaused = 0L
    persistAll()
  }

  fun onAppBackgrounded() {
    if (_state.value != TimerState.RUNNING) return
    val end = endEpochMillis ?: return
    val seconds = ((end - nowEpochMillis()).coerceAtLeast(0L)).toDouble() / 1000.0
    notifications.cancelElapsedNotification()
    notifications.scheduleElapsedNotification(timeIntervalSeconds = seconds)
  }

  fun onAppForegrounded() {
    notifications.cancelElapsedNotification()
    refreshFromClock(playElapsedInForeground = false)
    if (_state.value == TimerState.RUNNING) startTicker()
  }

  fun refreshFromClock(playElapsedInForeground: Boolean) {
    when (_state.value) {
      TimerState.RUNNING -> {
        val end = endEpochMillis
        if (end == null) {
          pause()
          return
        }
        val remaining = end - nowEpochMillis()
        if (remaining <= 0L) {
          elapse(playElapsedInForeground = playElapsedInForeground)
        } else {
          _duration.value = remaining.milliseconds
          persistAll()
        }
      }
      else -> Unit
    }
  }

  private fun pause() {
    notifications.cancelElapsedNotification()
    stopTicker()

    val end = endEpochMillis ?: return
    val remaining = (end - nowEpochMillis()).coerceAtLeast(0L)
    if (remaining == 0L) {
      elapse(playElapsedInForeground = true)
      return
    }

    remainingMillisWhenPaused = remaining
    endEpochMillis = null
    _duration.value = remaining.milliseconds
    _state.value = TimerState.PAUSED
    persistAll()
  }

  private fun resume() {
    if (_duration.value.inWholeMilliseconds <= 0L) return
    stopTicker()

    val now = nowEpochMillis()
    val remaining =
      remainingMillisWhenPaused.takeIf { it > 0L } ?: _duration.value.inWholeMilliseconds

    endEpochMillis = now + remaining
    _state.value = TimerState.RUNNING
    persistAll()
    startTicker()
  }

  private fun updateDuration(delta: Duration) {
    if (_state.value == TimerState.ELAPSED) return

    val deltaMillis = delta.inWholeMilliseconds
    if (deltaMillis == 0L) return

    when (_state.value) {
      TimerState.RUNNING -> {
        val end = endEpochMillis ?: return
        val newEnd = end + deltaMillis
        val remaining = newEnd - nowEpochMillis()
        if (remaining <= 0L) return
        endEpochMillis = newEnd
        remainingMillisWhenPaused = remaining
        _duration.value = remaining.milliseconds
        persistAll()
      }
      TimerState.PAUSED -> {
        val current = _duration.value.inWholeMilliseconds
        val newRemaining = current + deltaMillis
        if (newRemaining <= 0L) return
        remainingMillisWhenPaused = newRemaining
        _duration.value = newRemaining.milliseconds
        persistAll()
      }
      TimerState.IDLE -> Unit
      TimerState.ELAPSED -> Unit
    }
  }

  private fun elapse(playElapsedInForeground: Boolean) {
    notifications.cancelElapsedNotification()
    stopTicker()

    endEpochMillis = null
    remainingMillisWhenPaused = 0L
    _duration.value = Duration.ZERO
    _state.value = TimerState.ELAPSED
    persistAll()

    if (playElapsedInForeground) {
      try {
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
      } catch (t: Throwable) {
        Napier.e("Failed to vibrate on elapsed", t)
      }
    }
  }

  private fun startTicker() {
    if (_state.value != TimerState.RUNNING) return
    if (tickerJob?.isActive == true) return

    tickerJob = scope.launch {
      while (isActive && _state.value == TimerState.RUNNING) {
        refreshFromClock(playElapsedInForeground = true)
        delay(TICK_PERIOD_MILLIS)
      }
    }
  }

  private fun stopTicker() {
    tickerJob?.cancel()
    tickerJob = null
  }

  private fun persistAll() {
    userDefaults.setInteger(_state.value.ordinal.toLong(), forKey = KEY_STATE_ORDINAL)
    userDefaults.setDouble(
      _duration.value.inWholeMilliseconds.toDouble(),
      forKey = KEY_DURATION_MILLIS,
    )
    userDefaults.setDouble(
      _initialDuration.value.inWholeMilliseconds.toDouble(),
      forKey = KEY_INITIAL_MILLIS,
    )
    userDefaults.setDouble((endEpochMillis ?: 0L).toDouble(), forKey = KEY_END_EPOCH_MILLIS)
    userDefaults.setDouble(
      remainingMillisWhenPaused.toDouble(),
      forKey = KEY_REMAINING_PAUSED_MILLIS,
    )
  }

  private fun loadState(): TimerState {
    val ordinal = userDefaults.integerForKey(KEY_STATE_ORDINAL).toInt()
    return TimerState.entries.getOrNull(ordinal) ?: TimerState.IDLE
  }

  private fun loadDuration(): Duration =
    userDefaults.doubleForKey(KEY_DURATION_MILLIS).toLong().milliseconds

  private fun loadInitialDuration(): Duration =
    userDefaults.doubleForKey(KEY_INITIAL_MILLIS).toLong().milliseconds

  private fun loadEndEpochMillis(): Long? {
    val value = userDefaults.doubleForKey(KEY_END_EPOCH_MILLIS).toLong()
    return value.takeIf { it > 0L }
  }

  private fun loadRemainingMillisWhenPaused(): Long =
    max(0L, userDefaults.doubleForKey(KEY_REMAINING_PAUSED_MILLIS).toLong())

  private fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()

  companion object {
    private const val TICK_PERIOD_MILLIS: Long = 50L

    private const val KEY_STATE_ORDINAL = "timer_state_ordinal"
    private const val KEY_DURATION_MILLIS = "timer_duration_millis"
    private const val KEY_INITIAL_MILLIS = "timer_initial_millis"
    private const val KEY_END_EPOCH_MILLIS = "timer_end_epoch_millis"
    private const val KEY_REMAINING_PAUSED_MILLIS = "timer_remaining_paused_millis"
  }
}
