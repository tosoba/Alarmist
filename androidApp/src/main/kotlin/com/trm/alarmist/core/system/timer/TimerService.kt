package com.trm.alarmist.core.system.timer

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.cancel
import alarmist.composeapp.generated.resources.pause
import alarmist.composeapp.generated.resources.resume
import alarmist.composeapp.generated.resources.timer
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Binder
import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.trm.alarmist.MainActivity
import com.trm.alarmist.app.R
import com.trm.alarmist.core.common.util.getParcelable
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.common.util.toNotificationFormat
import com.trm.alarmist.core.domain.model.TimerState
import com.trm.alarmist.feature.root.RootStartMode
import io.github.aakira.napier.Napier
import kotlinx.parcelize.Parcelize
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TimerService : Service() {
  var state by mutableStateOf(TimerState.IDLE)
    private set

  var duration by mutableStateOf(Duration.ZERO)
    private set

  var initialDuration by mutableStateOf(Duration.ZERO)
    private set

  private var timer: Timer? = null
  private var mediaPlayer: MediaPlayer? = null
  private var showNotification = false

  private val binder = TimerBinder()

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaPlayer?.stopAndRelease()
  }

  override fun onBind(intent: Intent?): TimerBinder = binder

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    intent?.getParcelable<Action>(EXTRA_ACTION)?.let(::handleAction)
      ?: throw IllegalArgumentException("TimerService.Action extra is missing.")
    return super.onStartCommand(intent, flags, startId)
  }

  private fun handleAction(action: Action) {
    when (action) {
      is Action.Start -> {
        handleStartAction(action.duration)
      }
      is Action.ToggleRunning -> {
        handleToggleRunningAction()
      }
      is Action.AddDuration -> {
        updateDuration(duration + action.duration)
      }
      is Action.SubtractDuration -> {
        updateDuration(duration - action.duration)
      }
      Action.Reset -> {
        mediaPlayer?.stopAndRelease()
        pauseTimer()
        duration = initialDuration
      }
      Action.Cancel -> {
        cancelTimer()
        stopService()
      }
      Action.ShowNotification -> {
        showNotification = true
        startForeground()
      }
      Action.HideNotification -> {
        showNotification = false
        stopForeground()
      }
    }
  }

  private fun handleStartAction(duration: Duration) {
    initialDuration = duration
    startTimer(duration)
    if (showNotification) {
      startForeground()
    }
  }

  private fun handleToggleRunningAction() {
    if (state == TimerState.RUNNING) {
      pauseTimer()
      if (showNotification) {
        updateNotification(buildPausedNotification())
      }
    } else if (state == TimerState.PAUSED) {
      startTimer(duration)
      if (showNotification) {
        startForeground()
      }
    }
  }

  private fun startTimer(initialDuration: Duration) {
    duration = initialDuration
    timer =
      fixedRateTimer(period = TIMER_PERIOD_MILLIS) {
        duration -= TIMER_PERIOD_MILLIS.milliseconds
        if (duration.inWholeMilliseconds == 0L) {
          elapseTimer()
          playElapsedSound()
          stopForeground()
        } else if (showNotification && duration.inWholeMilliseconds % 1_000L == 0L) {
          updateNotification(buildRunningNotification())
        }
      }
    state = TimerState.RUNNING
  }

  private fun pauseTimer() {
    timer?.cancel()
    state = TimerState.PAUSED
  }

  private fun updateDuration(newDuration: Duration) {
    when {
      newDuration <= Duration.ZERO || state == TimerState.ELAPSED -> {
        return
      }
      state == TimerState.RUNNING -> {
        timer?.cancel()
        startTimer(newDuration)
      }
      else -> {
        duration = newDuration
      }
    }
  }

  private fun cancelTimer() {
    timer?.cancel()
    duration = Duration.ZERO
    state = TimerState.IDLE
  }

  private fun elapseTimer() {
    timer?.cancel()
    duration = Duration.ZERO
    state = TimerState.ELAPSED
  }

  private fun playElapsedSound() {
    mediaPlayer =
      MediaPlayer().apply {
        setOnErrorListener { player, what, extra ->
          Napier.e("Error occurred while playing audio - $what:$extra")
          player.stopAndRelease()
          true
        }

        setDataSource(
          this@TimerService,
          RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
        )

        setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )

        prepare()
        start()
      }
  }

  private fun MediaPlayer.stopAndRelease() {
    stop()
    release()
    mediaPlayer = null
  }

  private fun buildNotification(vararg actions: NotificationCompat.Action): Notification =
    NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
      .setContentTitle(getStringBlocking(Res.string.timer))
      .setContentText(duration.toNotificationFormat())
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setOngoing(true)
      .apply { actions.forEach(::addAction) }
      .setContentIntent(clickPendingIntent(this))
      .build()

  private fun startForeground() {
    startForeground(
      NOTIFICATION_ID,
      if (state == TimerState.RUNNING) buildRunningNotification() else buildPausedNotification(),
    )
  }

  private fun stopService() {
    stopForeground()
    stopSelf()
  }

  private fun stopForeground() {
    getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    stopForeground(STOP_FOREGROUND_REMOVE)
  }

  private fun createNotificationChannel() {
    getSystemService(NotificationManager::class.java)
      .createNotificationChannel(
        NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          NOTIFICATION_CHANNEL_NAME,
          NotificationManager.IMPORTANCE_LOW,
        )
      )
  }

  private fun updateNotification(notification: Notification) {
    getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
  }

  private fun buildRunningNotification(): Notification =
    buildNotification(pauseNotificationAction(), cancelNotificationAction())

  private fun buildPausedNotification(): Notification =
    buildNotification(resumeNotificationAction(), cancelNotificationAction())

  private fun pauseNotificationAction(): NotificationCompat.Action =
    NotificationCompat.Action(
      null,
      getStringBlocking(Res.string.pause),
      toggleRunningPendingIntent(this),
    )

  private fun resumeNotificationAction(): NotificationCompat.Action =
    NotificationCompat.Action(
      null,
      getStringBlocking(Res.string.resume),
      toggleRunningPendingIntent(this),
    )

  private fun cancelNotificationAction(): NotificationCompat.Action =
    NotificationCompat.Action(null, getStringBlocking(Res.string.cancel), cancelPendingIntent(this))

  private fun clickPendingIntent(context: Context): PendingIntent =
    PendingIntent.getActivity(
      context,
      CLICK_REQUEST_CODE,
      MainActivity.intent(context = context, startMode = RootStartMode.Timer),
      PendingIntent.FLAG_MUTABLE,
    )

  private fun toggleRunningPendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      TOGGLE_RUNNING_REQUEST_CODE,
      Intent(context, TimerService::class.java).putExtra(EXTRA_ACTION, Action.ToggleRunning),
      PendingIntent.FLAG_IMMUTABLE,
    )

  private fun cancelPendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      CANCEL_REQUEST_CODE,
      Intent(context, TimerService::class.java).putExtra(EXTRA_ACTION, Action.Cancel),
      PendingIntent.FLAG_IMMUTABLE,
    )

  inner class TimerBinder : Binder() {
    fun getService(): TimerService = this@TimerService
  }

  sealed interface Action : Parcelable {
    @Parcelize data class Start(val duration: Duration) : Action

    @Parcelize data object ToggleRunning : Action

    @Parcelize data class AddDuration(val duration: Duration) : Action

    @Parcelize data class SubtractDuration(val duration: Duration) : Action

    @Parcelize data object Reset : Action

    @Parcelize data object Cancel : Action

    @Parcelize data object ShowNotification : Action

    @Parcelize data object HideNotification : Action
  }

  companion object {
    private const val CLICK_REQUEST_CODE = 200
    private const val CANCEL_REQUEST_CODE = 201
    private const val TOGGLE_RUNNING_REQUEST_CODE = 202

    private const val NOTIFICATION_CHANNEL_ID = "TIMER_NOTIFICATION_ID"
    private const val NOTIFICATION_CHANNEL_NAME = "TIMER_NOTIFICATION"
    private const val NOTIFICATION_ID = -300

    private const val TIMER_PERIOD_MILLIS = 10L

    private const val EXTRA_ACTION = "action"

    fun startWithAction(context: Context, action: Action) {
      context.startService(Intent(context, TimerService::class.java).putExtra(EXTRA_ACTION, action))
    }
  }
}
