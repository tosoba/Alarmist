package com.trm.alarmist.core.system

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
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
    scheduledOnDaysOfWeek: Collection<DayOfWeek> = emptyList(),
    scheduledOnDates: Collection<LocalDate> = emptyList(),
    offOnDates: Collection<LocalDate> = emptyList(),
  )

  fun cancelAlarm(id: Long)
}
