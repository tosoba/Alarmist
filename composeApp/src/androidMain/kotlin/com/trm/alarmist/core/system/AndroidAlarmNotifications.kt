package com.trm.alarmist.core.system

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.trm.alarmist.R

fun Context.notifyAlarmFired() {
  val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  notificationManager.notify(
    1,
    NotificationCompat.Builder(this, ALARM_NOTIFICATION_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("Alarm was fired")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .build(),
  )
}

fun Application.createAlarmNotificationChannel() {
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
  val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  notificationManager.createNotificationChannel(
    NotificationChannel(
      ALARM_NOTIFICATION_CHANNEL_ID,
      ALARM_NOTIFICATION_CHANNEL_NAME,
      NotificationManager.IMPORTANCE_HIGH,
    )
  )
}

internal const val ALARM_NOTIFICATION_CHANNEL_ID = "ALARM_CHANNEL"
internal const val ALARM_NOTIFICATION_CHANNEL_NAME = "Alarms"
