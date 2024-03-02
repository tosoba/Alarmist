package com.trm.alarmist.core.system

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.trm.alarmist.R

fun Context.notifyAlarmFired(id: Int) {
  // TODO: use:
  // https://developer.android.com/develop/ui/views/notifications/build-notification#urgent-message
  // for full screen time-sensitive notification
  getSystemService(NotificationManager::class.java)
    .notify(
      id,
      NotificationCompat.Builder(this, ALARM_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Alarm was fired")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build(),
    )
}

fun Context.notifyAlarmUpcoming(id: Int) { // TODO: pass next fireAt
  getSystemService(NotificationManager::class.java)
    .notify(
      id,
      NotificationCompat.Builder(
          this,
          ALARM_NOTIFICATION_CHANNEL_ID,
        ) // TODO: add a dismiss button to cancel alarm - updateAlarmOnDismissedUseCase
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Alarm is upcoming")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSilent(true)
        .build(),
    )
}

fun Application.createAlarmNotificationChannel() {
  getSystemService(NotificationManager::class.java)
    .createNotificationChannel(
      NotificationChannel(
        ALARM_NOTIFICATION_CHANNEL_ID,
        ALARM_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH,
      )
    )
}

internal const val ALARM_NOTIFICATION_CHANNEL_ID = "ALARM_CHANNEL"
internal const val ALARM_NOTIFICATION_CHANNEL_NAME = "Alarms"
