package com.trm.alarmist.app.core.system.alarm

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.missed_alarm
import alarmist.composeapp.generated.resources.missed_multiple_alarms
import alarmist.composeapp.generated.resources.missed_multiple_alarms_most_recent
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
import com.trm.alarmist.app.R
import com.trm.alarmist.core.common.domain.model.AlarmFireSettings
import com.trm.alarmist.core.common.util.formatted
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.app.core.system.alarm.receiver.AlarmDismissedBroadcastReceiver
import kotlinx.datetime.LocalDateTime

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
        .setContentText(alarmNotificationContentText(settings.fireOnDateTime, settings.name))
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

fun Context.notifyAlarmMissed(id: Int, fireOnDateTime: LocalDateTime, name: String?) {
  if (
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
      PackageManager.PERMISSION_GRANTED
  ) {
    return
  }

  getSystemService(NotificationManager::class.java)
    .notify(
      id,
      NotificationCompat.Builder(this, ALARM_MISSED_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(getStringBlocking(Res.string.missed_alarm))
        .setContentText(alarmNotificationContentText(fireOnDateTime, name))
        .setSilent(true)
        .build(),
    )
}

fun Context.notifyMultipleAlarmsMissed(
  id: Int,
  fireOnDateTimes: List<LocalDateTime>,
  name: String?,
) {
  if (
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
      PackageManager.PERMISSION_GRANTED
  ) {
    return
  }

  getSystemService(NotificationManager::class.java)
    .notify(
      id,
      NotificationCompat.Builder(this, ALARM_MISSED_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(getStringBlocking(Res.string.missed_multiple_alarms, fireOnDateTimes.size))
        .setContentText(
          "${getStringBlocking(Res.string.missed_multiple_alarms_most_recent)} ${
            alarmNotificationContentText(
              fireOnDateTimes.first(),
              name,
            )
          }"
        )
        .setSilent(true)
        .build(),
    )
}

private fun Context.alarmNotificationContentText(
  fireOnDateTime: LocalDateTime,
  name: String?,
): String = buildString {
  append(fireOnDateTime.formatted(this@alarmNotificationContentText))
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
