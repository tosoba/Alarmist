package com.trm.alarmist.core.system

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
  private val alarmManager = context.getSystemService(AlarmManager::class.java)

  override fun scheduleAlarm(id: Long, fireOnDateTime: LocalDateTime) {
    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      fireOnDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
      alarmPendingIntent(id),
    )
  }

  override fun cancelAlarm(id: Long) {
    alarmManager.cancel(alarmPendingIntent(id))
  }

  private fun alarmPendingIntent(id: Long): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      AlarmBroadcastReceiver.alarmFiredIntent(context, id),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
