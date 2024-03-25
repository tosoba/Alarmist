package com.trm.alarmist.core.system

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
  private val alarmManager = context.getSystemService(AlarmManager::class.java)

  override fun scheduleAlarm(id: Long, fireOnDateTime: LocalDateTime) {
    context.cancelNotification(id.toInt())

    alarmManager.setExact(
      AlarmManager.RTC,
      fireOnDateTime
        .toInstant(TimeZone.currentSystemDefault())
        .minus(1, DateTimeUnit.HOUR) // TODO: this should be in alarm settings
        .toEpochMilliseconds(),
      alarmUpcomingPendingIntent(id, fireOnDateTime),
    )

    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      fireOnDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
      alarmFiredPendingIntent(id, fireOnDateTime),
    )
  }

  override fun cancelAlarm(id: Long) {
    context.cancelNotification(id.toInt())

    alarmManager.cancel(alarmFiredPendingIntent(id))
  }

  private fun alarmFiredPendingIntent(id: Long, fireOnDateTime: LocalDateTime): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      AlarmBroadcastReceiver.alarmFiredIntent(context, id, fireOnDateTime),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  // used only for alarm cancellation - no need to pass extras
  private fun alarmFiredPendingIntent(id: Long): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      AlarmBroadcastReceiver.alarmFiredIntent(context),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  private fun alarmUpcomingPendingIntent(id: Long, fireOnDateTime: LocalDateTime): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      AlarmBroadcastReceiver.alarmUpcomingIntent(context, id, fireOnDateTime),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
