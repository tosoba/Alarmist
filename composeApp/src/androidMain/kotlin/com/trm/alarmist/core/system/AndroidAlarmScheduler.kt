package com.trm.alarmist.core.system

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.trm.alarmist.core.system.receiver.AlarmFiredBroadcastReceiver
import com.trm.alarmist.core.system.receiver.AlarmUpcomingBroadcastReceiver
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
  private val alarmManager = context.getSystemService(AlarmManager::class.java)

  override fun scheduleAlarm(
    id: Long,
    fireOnDateTime: LocalDateTime,
    snoozeAvailable: Boolean,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
  ) {
    context.cancelNotification(id.toInt())

    val settings =
      AlarmFireSettings(
        id = id,
        fireOnDateTime = fireOnDateTime,
        snoozeAvailable = snoozeAvailable,
        alarmDurationMinutes = alarmDurationMinutes,
        soundEnabled = soundEnabled,
        vibrationEnabled = vibrationEnabled,
      )

    if (reminderOffsetHours > 0L) {
      alarmManager.setExact(
        AlarmManager.RTC,
        fireOnDateTime
          .toInstant(TimeZone.currentSystemDefault())
          .minus(reminderOffsetHours, DateTimeUnit.HOUR)
          .toEpochMilliseconds(),
        alarmUpcomingPendingIntent(settings),
      )
    }

    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      fireOnDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
      alarmFiredPendingIntent(settings),
    )
  }

  override fun cancelAlarm(id: Long) {
    context.cancelNotification(id.toInt())

    alarmManager.cancel(cancelAlarmFiredPendingIntent(id))
  }

  private fun alarmFiredPendingIntent(settings: AlarmFireSettings): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      settings.id.toInt(),
      AlarmFiredBroadcastReceiver.intent(context, settings),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  // used only for alarm cancellation - no need to pass extras
  private fun cancelAlarmFiredPendingIntent(id: Long): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      AlarmFiredBroadcastReceiver.intent(context),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

  private fun alarmUpcomingPendingIntent(settings: AlarmFireSettings): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      settings.id.toInt(),
      AlarmUpcomingBroadcastReceiver.intent(context, settings),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
