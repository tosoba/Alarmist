package com.trm.alarmist.core.system.stopwatch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.trm.alarmist.R
import com.trm.alarmist.core.domain.model.StopwatchState
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class StopwatchService : Service() {

  private val notificationBuilder: NotificationCompat.Builder by lazy {
    NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
      .setContentTitle("Stopwatch")
      .setContentText("00:00:00")
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setOngoing(true)
      .addAction(0, "Stop", ServiceHelper.stopPendingIntent(this))
      .addAction(0, "Cancel", ServiceHelper.cancelPendingIntent(this))
      .setContentIntent(ServiceHelper.clickPendingIntent(this))
  }

  private val binder = StopwatchBinder()

  private var duration: Duration = Duration.ZERO
  private lateinit var timer: Timer

  var seconds = mutableStateOf("00")
    private set

  var minutes = mutableStateOf("00")
    private set

  var hours = mutableStateOf("00")
    private set

  var currentState = mutableStateOf(StopwatchState.Idle)
    private set

  override fun onBind(intent: Intent?) = binder

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.getStringExtra(Constants.STOPWATCH_STATE)) {
      StopwatchState.Started.name -> {
        setStopButton()
        startForegroundService()
        startStopwatch { hours, minutes, seconds ->
          updateNotification(hours = hours, minutes = minutes, seconds = seconds)
        }
      }
      StopwatchState.Stopped.name -> {
        stopStopwatch()
        setResumeButton()
      }
      StopwatchState.Canceled.name -> {
        stopStopwatch()
        cancelStopwatch()
        stopForegroundService()
      }
    }
    intent?.action.let {
      when (it) {
        Constants.ACTION_SERVICE_START -> {
          setStopButton()
          startForegroundService()
          startStopwatch { hours, minutes, seconds ->
            updateNotification(hours = hours, minutes = minutes, seconds = seconds)
          }
        }
        Constants.ACTION_SERVICE_STOP -> {
          stopStopwatch()
          setResumeButton()
        }
        Constants.ACTION_SERVICE_CANCEL -> {
          stopStopwatch()
          cancelStopwatch()
          stopForegroundService()
        }
      }
    }
    return super.onStartCommand(intent, flags, startId)
  }

  private fun startStopwatch(onTick: (h: String, m: String, s: String) -> Unit) {
    currentState.value = StopwatchState.Started
    timer =
      fixedRateTimer(initialDelay = 1000L, period = 1000L) {
        duration = duration.plus(1.seconds)
        updateTimeUnits()
        onTick(hours.value, minutes.value, seconds.value)
      }
  }

  private fun stopStopwatch() {
    if (this::timer.isInitialized) {
      timer.cancel()
    }
    currentState.value = StopwatchState.Stopped
  }

  private fun cancelStopwatch() {
    duration = Duration.ZERO
    currentState.value = StopwatchState.Idle
    updateTimeUnits()
  }

  private fun updateTimeUnits() {
    duration.toComponents { hours, minutes, seconds, _ ->
      this@StopwatchService.hours.value = hours.toInt().pad()
      this@StopwatchService.minutes.value = minutes.pad()
      this@StopwatchService.seconds.value = seconds.pad()
    }
  }

  private fun Int.pad(): String {
    return this.toString().padStart(2, '0')
  }

  private fun startForegroundService() {
    createNotificationChannel()
    startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())
  }

  private fun stopForegroundService() {
    getSystemService(NotificationManager::class.java).cancel(Constants.NOTIFICATION_ID)
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
  }

  private fun createNotificationChannel() {
    val channel =
      NotificationChannel(
        Constants.NOTIFICATION_CHANNEL_ID,
        Constants.NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW,
      )
    getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
  }

  private fun updateNotification(hours: String, minutes: String, seconds: String) {
    getSystemService(NotificationManager::class.java)
      .notify(
        Constants.NOTIFICATION_ID,
        notificationBuilder
          .setContentText(formatTime(hours = hours, minutes = minutes, seconds = seconds))
          .build(),
      )
  }

  private fun formatTime(seconds: String, minutes: String, hours: String): String {
    return "$hours:$minutes:$seconds"
  }

  private fun setStopButton() {
    notificationBuilder.mActions.removeAt(0)
    notificationBuilder.mActions.add(
      0,
      NotificationCompat.Action(0, "Stop", ServiceHelper.stopPendingIntent(this)),
    )
    getSystemService(NotificationManager::class.java)
      .notify(Constants.NOTIFICATION_ID, notificationBuilder.build())
  }

  private fun setResumeButton() {
    notificationBuilder.mActions.removeAt(0)
    notificationBuilder.mActions.add(
      0,
      NotificationCompat.Action(0, "Resume", ServiceHelper.resumePendingIntent(this)),
    )
    getSystemService(NotificationManager::class.java)
      .notify(Constants.NOTIFICATION_ID, notificationBuilder.build())
  }

  inner class StopwatchBinder : Binder() {
    fun getService(): StopwatchService = this@StopwatchService
  }
}
