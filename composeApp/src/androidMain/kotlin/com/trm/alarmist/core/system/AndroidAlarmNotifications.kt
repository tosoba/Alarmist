package com.trm.alarmist.core.system

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.system.receiver.AlarmDismissedBroadcastReceiver
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
fun Context.notifyAlarmUpcoming(
  id: Long,
  fireOnDateTime: LocalDateTime,
) { // TODO: show formatted fireOnDateTime in notification
  getSystemService(NotificationManager::class.java)
    .notify(
      id.toInt(),
      NotificationCompat.Builder(this, ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Alarm is upcoming")
        .setSilent(true)
        .addAction(
          R.drawable.ic_launcher_foreground,
          getStringBlocking(Res.string.dismiss),
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

fun Context.notifyAlarmMissed(
  id: Long,
  fireOnDateTime: LocalDateTime,
) { // TODO: show formatted fireOnDateTime in notification
  getSystemService(NotificationManager::class.java)
    .notify(
      id.toInt(),
      NotificationCompat.Builder(this, ALARM_MISSED_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Alarm was missed")
        .setSilent(true)
        .build(),
    )
}

fun Context.cancelNotification(id: Int) {
  getSystemService(NotificationManager::class.java).cancel(id)
}

fun Application.createAlarmNotificationChannels() {
  createAlarmUpcomingNotificationChannel()
  createAlarmFiredNotificationChannel()
  createAlarmMissedNotificationChannel()
}

private fun Application.createAlarmUpcomingNotificationChannel() {
  getSystemService(NotificationManager::class.java)
    .createNotificationChannel(
      NotificationChannel(
        ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID,
        ALARM_UPCOMING_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT,
      )
    )
}

private const val ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID = "ALARM_UPCOMING_CHANNEL"
private const val ALARM_UPCOMING_NOTIFICATION_CHANNEL_NAME = "Upcoming alarms"

private fun Application.createAlarmFiredNotificationChannel() {
  getSystemService(NotificationManager::class.java)
    .createNotificationChannel(
      NotificationChannel(
          ALARM_FIRED_NOTIFICATION_CHANNEL_ID,
          ALARM_FIRED_NOTIFICATION_CHANNEL_NAME,
          NotificationManager.IMPORTANCE_HIGH,
        )
        .apply { setSound(null, null) }
    )
}

internal const val ALARM_FIRED_NOTIFICATION_CHANNEL_ID = "ALARM_FIRED_CHANNEL"
private const val ALARM_FIRED_NOTIFICATION_CHANNEL_NAME = "Fired alarms"

private fun Application.createAlarmMissedNotificationChannel() {
  getSystemService(NotificationManager::class.java)
    .createNotificationChannel(
      NotificationChannel(
        ALARM_MISSED_NOTIFICATION_CHANNEL_ID,
        ALARM_MISSED_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH,
      )
    )
}

internal const val ALARM_MISSED_NOTIFICATION_CHANNEL_ID = "ALARM_MISSED_CHANNEL"
private const val ALARM_MISSED_NOTIFICATION_CHANNEL_NAME = "Missed alarms"
