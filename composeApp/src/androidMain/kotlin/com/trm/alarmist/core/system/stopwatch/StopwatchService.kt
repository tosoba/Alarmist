package com.trm.alarmist.core.system.stopwatch

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.cancel
import alarmist.composeapp.generated.resources.pause
import alarmist.composeapp.generated.resources.resume
import alarmist.composeapp.generated.resources.stopwatch
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
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.common.util.zeroPadded
import com.trm.alarmist.core.domain.model.StopwatchState
import com.trm.alarmist.feature.root.RootStartMode
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class StopwatchService : Service() {
  var state by mutableStateOf(StopwatchState.IDLE)
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
          cancelStopwatch()
          stopForegroundService()
        }
      }
    } ?: throw IllegalArgumentException("Missing StopwatchServiceAction.")

    return super.onStartCommand(intent, flags, startId)
  }

  private fun startStopwatch() {
    state = StopwatchState.STARTED
    timer =
      fixedRateTimer(period = TIMER_PERIOD_MILLIS) {
        duration += TIMER_PERIOD_MILLIS.milliseconds
        if (duration.inWholeMilliseconds % 1_000L == 0L) updateNotificationWhenStarted()
      }
  }

  private fun stopStopwatch() {
    timer?.cancel()
    state = StopwatchState.STOPPED
  }

  private fun cancelStopwatch() {
    timer?.cancel()
    duration = Duration.ZERO
    state = StopwatchState.IDLE
  }

  private fun buildNotification(vararg actions: NotificationCompat.Action): Notification =
    NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
      .setContentTitle(getStringBlocking(Res.string.stopwatch))
      .setContentText(duration.formatted())
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
    NotificationCompat.Action(null, getStringBlocking(Res.string.pause), stopPendingIntent(this))

  private fun resumeNotificationAction(): NotificationCompat.Action =
    NotificationCompat.Action(null, getStringBlocking(Res.string.resume), resumePendingIntent(this))

  private fun cancelNotificationAction(): NotificationCompat.Action =
    NotificationCompat.Action(null, getStringBlocking(Res.string.cancel), cancelPendingIntent(this))

  private fun clickPendingIntent(context: Context): PendingIntent =
    PendingIntent.getActivity(
      context,
      CLICK_REQUEST_CODE,
      Intent(context, MainActivity::class.java)
        .putExtra(RootStartMode.EXTRA_KEY, RootStartMode.Stopwatch),
      PendingIntent.FLAG_MUTABLE,
    )

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

  private fun Duration.formatted(): String = toComponents { hours, minutes, seconds, _ ->
    "${hours.toInt().zeroPadded()}:${minutes.zeroPadded()}:${seconds.zeroPadded()}"
  }

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
    private const val NOTIFICATION_ID = -200

    private const val TIMER_PERIOD_MILLIS = 10L

    fun start(context: Context, action: Action) {
      context.startService(Intent(context, StopwatchService::class.java).setAction(action.name))
    }
  }
}
