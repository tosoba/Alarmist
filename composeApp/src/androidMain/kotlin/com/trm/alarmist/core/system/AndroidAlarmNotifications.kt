package com.trm.alarmist.core.system

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.missed_alarm
import alarmist.composeapp.generated.resources.upcoming_alarm
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.system.receiver.AlarmDismissedBroadcastReceiver
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
fun Context.notifyAlarmUpcoming(settings: AlarmFireSettings) {
  getSystemService(NotificationManager::class.java)
    .notify(
      settings.id.toInt(),
      NotificationCompat.Builder(this, ALARM_UPCOMING_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(getStringBlocking(Res.string.upcoming_alarm))
        .setContentText(settings.fireOnDateTime.formattedContentText(this))
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

@OptIn(ExperimentalResourceApi::class)
fun Context.notifyAlarmMissed(settings: AlarmFireSettings) {
  getSystemService(NotificationManager::class.java)
    .notify(
      settings.id.toInt(),
      NotificationCompat.Builder(this, ALARM_MISSED_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(getStringBlocking(Res.string.missed_alarm))
        .setContentText(settings.fireOnDateTime.formattedContentText(this))
        .setSilent(true)
        .build(),
    )
}

private fun LocalDateTime.formattedContentText(context: Context): String =
  "${dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${time.format(
    LocalTime.Format {
      if (!DateFormat.is24HourFormat(context)) amPmHour(padding = Padding.ZERO) else hour(padding = Padding.ZERO)
      char(':')
      minute(padding = Padding.ZERO)
      if (!DateFormat.is24HourFormat(context)) {
        char(' ')
        amPmMarker("AM", "PM")
      }
    }
  )
  }"

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
