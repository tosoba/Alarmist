package com.trm.alarmist.core.system.alarm

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.missed_alarm
import alarmist.composeapp.generated.resources.upcoming_alarm
import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.trm.alarmist.R
import com.trm.alarmist.core.common.domain.model.AlarmFireSettings
import com.trm.alarmist.core.common.util.formattedTime
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.system.alarm.receiver.AlarmDismissedBroadcastReceiver

fun Context.notifyAlarmUpcoming(settings: AlarmFireSettings) {
  if (
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
      PackageManager.PERMISSION_GRANTED
  ) {
    return
  }

  getSystemService(NotificationManager::class.java)
    .notify(
      settings.id.toInt(),
      NotificationCompat.Builder(this, ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(getStringBlocking(Res.string.upcoming_alarm))
        .setContentText(settings.notificationContentText(this))
        .setSilent(true)
        .addAction(
          R.drawable.ic_launcher_foreground,
          getStringBlocking(Res.string.dismiss),
          PendingIntent.getBroadcast(
            this,
            settings.id.toInt(),
            AlarmDismissedBroadcastReceiver.intent(this, settings),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
          ),
        )
        .build(),
    )
}

fun Context.notifyAlarmMissed(settings: AlarmFireSettings) {
  if (
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
      PackageManager.PERMISSION_GRANTED
  ) {
    return
  }

  getSystemService(NotificationManager::class.java)
    .notify(
      settings.id.toInt(),
      NotificationCompat.Builder(this, ALARM_MISSED_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(getStringBlocking(Res.string.missed_alarm))
        .setContentText(settings.notificationContentText(this))
        .setSilent(true)
        .build(),
    )
}

private fun AlarmFireSettings.notificationContentText(context: Context): String = buildString {
  append(fireOnDateTime.formattedTime(context))
  name?.let {
    append(" · ")
    append(it)
  }
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
