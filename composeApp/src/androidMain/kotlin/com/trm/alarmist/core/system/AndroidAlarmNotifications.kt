package com.trm.alarmist.core.system

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.dismiss
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
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString

fun Context.notifyAlarmFired(
  id: Long,
  fireOnDateTime: LocalDateTime,
) { // TODO: alarm name/group name etc.
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
        .addDismissAction(this, id, fireOnDateTime)
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
        .addDismissAction(this, id, fireOnDateTime)
        .build(),
    )
}

@OptIn(ExperimentalResourceApi::class)
private fun NotificationCompat.Builder.addDismissAction(
  context: Context,
  id: Long,
  fireOnDateTime: LocalDateTime,
): NotificationCompat.Builder =
  addAction(
    R.drawable.ic_launcher_foreground,
    runBlocking { getString(Res.string.dismiss) },
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      AlarmDismissedBroadcastReceiver.intent(context, id, fireOnDateTime),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    ),
  )

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
