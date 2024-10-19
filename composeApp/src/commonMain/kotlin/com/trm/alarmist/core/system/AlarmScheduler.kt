package com.trm.alarmist.core.system

import kotlinx.datetime.LocalDateTime

interface AlarmScheduler {
  fun scheduleNextWidgetUpdate()

  fun scheduleAlarm(
    id: Long,
    name: String?,
    fireOnDateTime: LocalDateTime,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    soundId: String?,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
  )

  fun cancelAlarm(id: Long)
}
