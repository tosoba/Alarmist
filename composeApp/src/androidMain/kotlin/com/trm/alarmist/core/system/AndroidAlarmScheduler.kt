package com.trm.alarmist.core.system

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
  private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

  override fun scheduleAlarm(id: Long, fireAt: LocalDateTime) {
    // TODO: any runtime permission checks depending on android version
    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      fireAt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
      alarmPendingIntent(id),
    )
  }

  private fun alarmPendingIntent(id: Long): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      id.toInt(),
      Intent(context, AlarmBroadcastReceiver::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
