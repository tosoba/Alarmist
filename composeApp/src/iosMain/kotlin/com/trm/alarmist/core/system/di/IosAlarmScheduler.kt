package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.LocalDateTime

class IosAlarmScheduler : AlarmScheduler {
  override fun scheduleAlarm(
    id: Long,
    name: String?,
    fireOnDateTime: LocalDateTime,
    snoozeAvailable: Boolean,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    soundId: String?,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
  ) {}

  override fun cancelAlarm(id: Long) {}
}
