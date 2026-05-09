package com.trm.alarmist.core.system.stopwatch

import com.trm.alarmist.core.domain.model.StopwatchState
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
import platform.Foundation.NSUserDefaults
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class IosStopwatchController(
  private val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults
) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  private val _state = MutableStateFlow(loadState())
  val state: StateFlow<StopwatchState> = _state.asStateFlow()

  private val _duration = MutableStateFlow(loadDuration())
  val duration: StateFlow<Duration> = _duration.asStateFlow()

  private val _laps = MutableStateFlow(loadLaps())
  val laps: StateFlow<List<Duration>> = _laps.asStateFlow()

  private var tickerJob: Job? = null

  private var startEpochMillis: Long? = loadStartEpochMillis()
  private var accumulatedMillis: Long = loadAccumulatedMillis()

  fun toggleRunning() {
    when (_state.value) {
      StopwatchState.RUNNING -> pause()
      StopwatchState.PAUSED,
      StopwatchState.IDLE -> startOrResume()
    }
  }

  fun cancel() {
    stopTicker()
    _state.value = StopwatchState.IDLE
    _duration.value = Duration.ZERO
    _laps.value = emptyList()
    startEpochMillis = null
    accumulatedMillis = 0L
    persistAll()
  }

  fun recordLap() {
    if (_state.value != StopwatchState.RUNNING) return
    refreshFromClock()
    _laps.value += _duration.value
    persistAll()
  }

  fun onAppForegrounded() {
    refreshFromClock()
    if (_state.value == StopwatchState.RUNNING) startTicker()
  }

  fun onAppBackgrounded() {
    stopTicker()
    persistAll()
  }

  fun refreshFromClock() {
    if (_state.value != StopwatchState.RUNNING) return
    val start =
      startEpochMillis
        ?: run {
          _state.value = StopwatchState.PAUSED
          persistAll()
          return
        }
    val now = nowEpochMillis()
    val elapsed = (accumulatedMillis + (now - start)).coerceAtLeast(0L)
    _duration.value = elapsed.milliseconds
  }

  private fun startOrResume() {
    if (_state.value == StopwatchState.IDLE) {
      accumulatedMillis = 0L
      _duration.value = Duration.ZERO
      _laps.value = emptyList()
    }

    startEpochMillis = nowEpochMillis()
    _state.value = StopwatchState.RUNNING
    persistAll()
    startTicker()
  }

  private fun pause() {
    val start = startEpochMillis
    if (start != null) {
      val now = nowEpochMillis()
      accumulatedMillis = max(0L, accumulatedMillis + (now - start))
      _duration.value = accumulatedMillis.milliseconds
    }
    startEpochMillis = null
    _state.value = StopwatchState.PAUSED
    stopTicker()
    persistAll()
  }

  private fun startTicker() {
    if (_state.value != StopwatchState.RUNNING) return
    if (tickerJob?.isActive == true) return

    tickerJob = scope.launch {
      while (isActive && _state.value == StopwatchState.RUNNING) {
        refreshFromClock()
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
    userDefaults.setDouble(accumulatedMillis.toDouble(), forKey = KEY_ACCUMULATED_MILLIS)
    userDefaults.setDouble((startEpochMillis ?: 0L).toDouble(), forKey = KEY_START_EPOCH_MILLIS)
    userDefaults.setObject(encodeLaps(_laps.value), forKey = KEY_LAPS)
  }

  private fun loadState(): StopwatchState {
    val ordinal = userDefaults.integerForKey(KEY_STATE_ORDINAL).toInt()
    return StopwatchState.entries.getOrNull(ordinal) ?: StopwatchState.IDLE
  }

  private fun loadDuration(): Duration =
    userDefaults.doubleForKey(KEY_DURATION_MILLIS).toLong().milliseconds

  private fun loadAccumulatedMillis(): Long =
    max(0L, userDefaults.doubleForKey(KEY_ACCUMULATED_MILLIS).toLong())

  private fun loadStartEpochMillis(): Long? {
    val value = userDefaults.doubleForKey(KEY_START_EPOCH_MILLIS).toLong()
    return value.takeIf { it > 0L }
  }

  private fun loadLaps(): List<Duration> = decodeLaps(userDefaults.stringForKey(KEY_LAPS))

  private fun encodeLaps(laps: List<Duration>): String =
    laps.joinToString(separator = ",") { it.inWholeMilliseconds.toString() }

  private fun decodeLaps(encoded: String?): List<Duration> {
    if (encoded.isNullOrBlank()) return emptyList()
    return encoded.split(',').mapNotNull { it.toLongOrNull() }.map { it.milliseconds }
  }

  private fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()

  companion object {
    private const val TICK_PERIOD_MILLIS: Long = 50L

    private const val KEY_STATE_ORDINAL = "stopwatch_state_ordinal"
    private const val KEY_DURATION_MILLIS = "stopwatch_duration_millis"
    private const val KEY_ACCUMULATED_MILLIS = "stopwatch_accumulated_millis"
    private const val KEY_START_EPOCH_MILLIS = "stopwatch_start_epoch_millis"
    private const val KEY_LAPS = "stopwatch_laps"
  }
}
