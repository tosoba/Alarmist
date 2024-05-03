package com.trm.alarmist.core.system

import kotlinx.datetime.LocalDateTime

interface AlarmScheduler {
  fun scheduleAlarm(
    id: Long,
    name: String?,
    fireOnDateTime: LocalDateTime,
    snoozeAvailable: Boolean,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    soundId: String?,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long
  )

  fun cancelAlarm(id: Long)
}
