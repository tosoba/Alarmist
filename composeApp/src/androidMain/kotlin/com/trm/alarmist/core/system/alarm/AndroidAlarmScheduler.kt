package com.trm.alarmist.core.system.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.trm.alarmist.core.common.domain.model.AlarmFireSettings
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.alarm.receiver.AlarmFiredBroadcastReceiver
import com.trm.alarmist.core.system.alarm.receiver.AlarmUpcomingBroadcastReceiver
import com.trm.alarmist.widget.common.system.WidgetUpdateAlarmReceiver
import kotlin.time.ExperimentalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
  private val alarmManager = context.getSystemService(AlarmManager::class.java)

  override fun scheduleNextWidgetUpdate() {
    alarmManager.setExact(
      AlarmManager.RTC,
      @OptIn(ExperimentalTime::class)
      LocalDate.now()
        .plus(1, DateTimeUnit.DAY)
        .atTime(0, 0, 1)
        .toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds(),
      WidgetUpdateAlarmReceiver.pendingIntent(context),
    )
  }

  @OptIn(ExperimentalTime::class)
  override fun scheduleAlarm(
    id: Long,
    name: String?,
    fireOnDateTime: LocalDateTime,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    soundId: String?,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
  ) {
    context.cancelNotification(id.toInt())

    val settings =
      AlarmFireSettings(
        id = id,
        name = name,
        fireOnDateTime = fireOnDateTime,
        alarmDurationMinutes = alarmDurationMinutes,
        soundEnabled = soundEnabled,
        soundId = soundId,
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
    alarmManager.cancel(cancelAlarmUpcomingPendingIntent(id))
    alarmManager.cancel(cancelAlarmFiredPendingIntent(id))
  }

  private fun alarmFiredPendingIntent(settings: AlarmFireSettings): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      settings.id.toInt(),
      AlarmFiredBroadcastReceiver.intent(context, settings),
      pendingIntentFlags(),
    )

  // used only for alarm cancellation - no need to pass extras
  private fun cancelAlarmFiredPendingIntent(id: Long): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      AlarmFiredBroadcastReceiver.intent(context),
      pendingIntentFlags(),
    )

  private fun alarmUpcomingPendingIntent(settings: AlarmFireSettings): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      settings.id.toInt(),
      AlarmUpcomingBroadcastReceiver.intent(context, settings),
      pendingIntentFlags(),
    )

  // used only for alarm cancellation - no need to pass extras
  private fun cancelAlarmUpcomingPendingIntent(id: Long): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      AlarmUpcomingBroadcastReceiver.intent(context),
      pendingIntentFlags(),
    )

  private fun pendingIntentFlags(): Int =
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
}
