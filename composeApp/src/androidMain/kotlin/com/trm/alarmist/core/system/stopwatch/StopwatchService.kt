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
import com.trm.alarmist.core.common.util.getSerializable
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.common.util.toNotificationFormat
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
    intent?.getSerializable<Action>(EXTRA_ACTION)?.let { handleAction(it) }
      ?: throw IllegalArgumentException("Missing StopwatchServiceAction.")
    return super.onStartCommand(intent, flags, startId)
  }

  private fun handleAction(action: Action) {
    when (action) {
      Action.TOGGLE_RUNNING -> {
        if (state == StopwatchState.RUNNING) {
          stopStopwatch()
          updateNotification(buildPausedNotification())
        } else {
          startForegroundService()
          startStopwatch()
        }
      }
      Action.CANCEL -> {
        cancelStopwatch()
        stopForegroundService()
      }
    }
  }

  private fun startStopwatch() {
    timer =
      fixedRateTimer(period = TIMER_PERIOD_MILLIS) {
        duration += TIMER_PERIOD_MILLIS.milliseconds
        if (duration.inWholeMilliseconds % 1_000L == 0L) {
          updateNotification(buildRunningNotification())
        }
      }
    state = StopwatchState.RUNNING
  }

  private fun stopStopwatch() {
    timer?.cancel()
    state = StopwatchState.PAUSED
  }

  private fun cancelStopwatch() {
    timer?.cancel()
    duration = Duration.ZERO
    state = StopwatchState.IDLE
  }

  private fun buildNotification(vararg actions: NotificationCompat.Action): Notification =
    NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
      .setContentTitle(getStringBlocking(Res.string.stopwatch))
      .setContentText(duration.toNotificationFormat())
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setOngoing(true)
      .apply { actions.forEach(::addAction) }
      .setContentIntent(clickPendingIntent(this))
      .build()

  private fun startForegroundService() {
    startForeground(NOTIFICATION_ID, buildRunningNotification())
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
      Intent(context, MainActivity::class.java)
        .putExtra(RootStartMode.EXTRA_KEY, RootStartMode.Stopwatch),
      PendingIntent.FLAG_MUTABLE,
    )

  private fun toggleRunningPendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      TOGGLE_RUNNING_REQUEST_CODE,
      Intent(context, StopwatchService::class.java).putExtra(EXTRA_ACTION, Action.TOGGLE_RUNNING),
      PendingIntent.FLAG_IMMUTABLE,
    )

  private fun cancelPendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      CANCEL_REQUEST_CODE,
      Intent(context, StopwatchService::class.java).putExtra(EXTRA_ACTION, Action.CANCEL),
      PendingIntent.FLAG_IMMUTABLE,
    )

  inner class StopwatchBinder : Binder() {
    fun getService(): StopwatchService = this@StopwatchService
  }

  enum class Action {
    TOGGLE_RUNNING,
    CANCEL,
  }

  companion object {
    private const val CLICK_REQUEST_CODE = 100
    private const val CANCEL_REQUEST_CODE = 101
    private const val TOGGLE_RUNNING_REQUEST_CODE = 102

    private const val NOTIFICATION_CHANNEL_ID = "STOPWATCH_NOTIFICATION_ID"
    private const val NOTIFICATION_CHANNEL_NAME = "STOPWATCH_NOTIFICATION"
    private const val NOTIFICATION_ID = -200

    private const val TIMER_PERIOD_MILLIS = 10L

    private const val EXTRA_ACTION = "action"

    fun startWithAction(context: Context, action: Action) {
      context.startService(
        Intent(context, StopwatchService::class.java).putExtra(EXTRA_ACTION, action)
      )
    }
  }
}
