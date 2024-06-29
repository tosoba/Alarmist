package com.trm.alarmist.core.system.stopwatch

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.trm.alarmist.MainActivity
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.zeroPadded
import com.trm.alarmist.core.domain.model.StopwatchState
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class StopwatchService : Service() {
  var state by mutableStateOf(StopwatchState.Idle)
    private set

  var duration by mutableStateOf(Duration.ZERO)
    private set

  private var timer: Timer? = null

  private val binder = StopwatchBinder()

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  override fun onBind(intent: Intent?): StopwatchBinder = binder

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    intent?.action?.let {
      when (Action.valueOf(it)) {
        Action.START -> {
          startForegroundService()
          startStopwatch()
        }
        Action.STOP -> {
          stopStopwatch()
          updateNotificationWhenStopped()
        }
        Action.CANCEL -> {
          stopStopwatch()
          cancelStopwatch()
          stopForegroundService()
        }
      }
    } ?: throw IllegalArgumentException("Missing StopwatchServiceAction.")

    return super.onStartCommand(intent, flags, startId)
  }

  private fun startStopwatch() {
    state = StopwatchState.Started
    timer =
      fixedRateTimer(initialDelay = 1000L, period = 1000L) {
        duration += 1.seconds
        updateNotificationWhenStarted()
      }
  }

  private fun stopStopwatch() {
    timer?.cancel()
    state = StopwatchState.Stopped
  }

  private fun cancelStopwatch() {
    duration = Duration.ZERO
    state = StopwatchState.Idle
  }

  private fun buildNotification(vararg actions: NotificationCompat.Action): Notification =
    NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
      .setContentTitle("Stopwatch")
      .setContentText(
        duration.toComponents { hours, minutes, seconds, _ ->
          formatTime(
            hours = hours.toInt().zeroPadded(),
            minutes = minutes.zeroPadded(),
            seconds = seconds.zeroPadded(),
          )
        }
      )
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setOngoing(true)
      .apply { actions.forEach(::addAction) }
      .setContentIntent(clickPendingIntent(this))
      .build()

  private fun startForegroundService() {
    startForeground(NOTIFICATION_ID, buildStartedNotification())
  }

  private fun stopForegroundService() {
    getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
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

  private fun updateNotificationWhenStarted() {
    getSystemService(NotificationManager::class.java)
      .notify(NOTIFICATION_ID, buildStartedNotification())
  }

  private fun updateNotificationWhenStopped() {
    getSystemService(NotificationManager::class.java)
      .notify(NOTIFICATION_ID, buildStoppedNotification())
  }

  private fun buildStartedNotification(): Notification =
    buildNotification(pauseNotificationAction(), cancelNotificationAction())

  private fun buildStoppedNotification(): Notification =
    buildNotification(resumeNotificationAction(), cancelNotificationAction())

  private fun pauseNotificationAction(): NotificationCompat.Action =
    NotificationCompat.Action(null, "Pause", stopPendingIntent(this))

  private fun resumeNotificationAction(): NotificationCompat.Action =
    NotificationCompat.Action(null, "Resume", resumePendingIntent(this))

  private fun cancelNotificationAction(): NotificationCompat.Action =
    NotificationCompat.Action(null, "Cancel", cancelPendingIntent(this))

  private fun formatTime(seconds: String, minutes: String, hours: String): String {
    return "$hours:$minutes:$seconds"
  }

  private fun clickPendingIntent(context: Context): PendingIntent {
    // TODO: deeplink to stopwatch?
    return PendingIntent.getActivity(
      context,
      CLICK_REQUEST_CODE,
      Intent(context, MainActivity::class.java),
      PendingIntent.FLAG_IMMUTABLE,
    )
  }

  private fun stopPendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      STOP_REQUEST_CODE,
      Intent(context, StopwatchService::class.java).setAction(Action.STOP.name),
      PendingIntent.FLAG_IMMUTABLE,
    )

  private fun resumePendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      RESUME_REQUEST_CODE,
      Intent(context, StopwatchService::class.java).setAction(Action.START.name),
      PendingIntent.FLAG_IMMUTABLE,
    )

  private fun cancelPendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      CANCEL_REQUEST_CODE,
      Intent(context, StopwatchService::class.java).setAction(Action.CANCEL.name),
      PendingIntent.FLAG_IMMUTABLE,
    )

  inner class StopwatchBinder : Binder() {
    fun getService(): StopwatchService = this@StopwatchService
  }

  enum class Action {
    START,
    STOP,
    CANCEL,
  }

  companion object {
    private const val CLICK_REQUEST_CODE = 100
    private const val CANCEL_REQUEST_CODE = 101
    private const val STOP_REQUEST_CODE = 102
    private const val RESUME_REQUEST_CODE = 103

    private const val NOTIFICATION_CHANNEL_ID = "STOPWATCH_NOTIFICATION_ID"
    private const val NOTIFICATION_CHANNEL_NAME = "STOPWATCH_NOTIFICATION"
    // TODO: change it to prevent collisions with alarm notifications
    private const val NOTIFICATION_ID = 10

    fun start(context: Context, action: Action) {
      context.startService(Intent(context, StopwatchService::class.java).setAction(action.name))
    }
  }
}
