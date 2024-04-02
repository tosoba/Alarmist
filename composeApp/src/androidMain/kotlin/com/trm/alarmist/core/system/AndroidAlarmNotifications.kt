package com.trm.alarmist.core.system

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.trm.alarmist.R
import com.trm.alarmist.core.system.receiver.AlarmDismissedBroadcastReceiver
import kotlinx.datetime.LocalDateTime

fun Context.notifyAlarmFired(id: Long) {
  // TODO: use:
  // https://developer.android.com/develop/ui/views/notifications/build-notification#urgent-message
  // for full screen time-sensitive notification
  getSystemService(NotificationManager::class.java)
    .notify(
      id.toInt(),
      NotificationCompat.Builder(this, ALARM_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Alarm was fired")
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
        .apply { flags or Notification.FLAG_INSISTENT },
    )
}

fun Context.notifyAlarmUpcoming(
  id: Long,
  fireOnDateTime: LocalDateTime,
) { // TODO: show formatted fireOnDateTime in notification
  getSystemService(NotificationManager::class.java)
    .notify(
      id.toInt(),
      NotificationCompat.Builder(this, ALARM_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Alarm is upcoming")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSilent(true)
        .addAction(
          R.drawable.ic_launcher_foreground,
          "Dismiss",
          PendingIntent.getBroadcast(
            this,
            id.toInt(),
            AlarmDismissedBroadcastReceiver.intent(this, id, fireOnDateTime),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
          ),
        )
        .build(),
    )
}

fun Context.cancelNotification(id: Int) {
  getSystemService(NotificationManager::class.java).cancel(id)
}

fun Application.createAlarmNotificationChannel() {
  getSystemService(NotificationManager::class.java)
    .createNotificationChannel(
      NotificationChannel(
        ALARM_NOTIFICATION_CHANNEL_ID,
        ALARM_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH, // TODO: IMPORTANCE_MAX?
      )
    )
}

internal const val ALARM_NOTIFICATION_CHANNEL_ID = "ALARM_CHANNEL"
internal const val ALARM_NOTIFICATION_CHANNEL_NAME = "Alarms"
