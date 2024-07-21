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
import android.os.Binder
import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.trm.alarmist.MainActivity
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.getParcelable
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.common.util.toNotificationFormat
import com.trm.alarmist.core.domain.model.TimerState
import com.trm.alarmist.feature.root.RootStartMode
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.parcelize.Parcelize

class TimerService : Service() {
  var state by mutableStateOf(TimerState.IDLE)
    private set

  var duration by mutableStateOf(Duration.ZERO)
    private set

  private var timer: Timer? = null

  private val binder = TimerBinder()

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  override fun onBind(intent: Intent?): TimerBinder = binder

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    intent?.getParcelable<Action>(EXTRA_ACTION)?.let {
      when (it) {
        is Action.Start -> {
          startForegroundService()
          startTimer(it.duration)
        }
        Action.Stop -> {
          stopTimer()
          updateNotification(buildStoppedNotification())
        }
        Action.Cancel -> {
          cancelTimer()
          stopForegroundService()
        }
      }
    } ?: throw IllegalArgumentException("Missing TimerServiceAction.")

    return super.onStartCommand(intent, flags, startId)
  }

  private fun startTimer(initialDuration: Duration) {
    duration = initialDuration
    state = TimerState.STARTED
    timer =
      fixedRateTimer(period = TIMER_PERIOD_MILLIS) {
        duration -= TIMER_PERIOD_MILLIS.milliseconds
        if (initialDuration.inWholeMilliseconds == 0L) {
          // TODO: play sound/vibrate on elapsed
          elapseTimer()
        } else if (initialDuration.inWholeMilliseconds % 1_000L == 0L) {
          updateNotification(buildStartedNotification())
        }
      }
  }

  private fun stopTimer() {
    timer?.cancel()
    state = TimerState.STOPPED
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

  private fun buildNotification(vararg actions: NotificationCompat.Action): Notification =
    NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
      .setContentTitle(getStringBlocking(Res.string.timer))
      .setContentText(duration.toNotificationFormat())
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

  private fun updateNotification(notification: Notification) {
    getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
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
        .putExtra(RootStartMode.EXTRA_KEY, RootStartMode.Timer),
      PendingIntent.FLAG_MUTABLE,
    )

  private fun stopPendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      STOP_REQUEST_CODE,
      Intent(context, TimerService::class.java).putExtra(EXTRA_ACTION, Action.Stop),
      PendingIntent.FLAG_IMMUTABLE,
    )

  private fun resumePendingIntent(context: Context): PendingIntent =
    PendingIntent.getService(
      context,
      RESUME_REQUEST_CODE,
      Intent(context, TimerService::class.java).putExtra(EXTRA_ACTION, Action.Start(duration)),
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

    @Parcelize data object Stop : Action

    @Parcelize data object Cancel : Action
  }

  companion object {
    private const val CLICK_REQUEST_CODE = 200
    private const val CANCEL_REQUEST_CODE = 201
    private const val STOP_REQUEST_CODE = 202
    private const val RESUME_REQUEST_CODE = 203

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
