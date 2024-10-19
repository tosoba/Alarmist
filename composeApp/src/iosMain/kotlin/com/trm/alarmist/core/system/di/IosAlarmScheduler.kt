package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.LocalDateTime

class IosAlarmScheduler : AlarmScheduler {
  override fun scheduleNextWidgetUpdate() {}

  override fun scheduleAlarm(
    id: Long,
    name: String?,
    fireOnDateTime: LocalDateTime,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    soundId: String?,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
  ) {}

  override fun cancelAlarm(id: Long) {}
}
