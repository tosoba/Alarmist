package com.trm.alarmist.core.util

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

fun alarmModel(
  id: Long = 1L,
  groupId: Long? = null,
  fireAtTime: LocalTime = LocalTime.now(),
  name: String? = null,
  isOn: Boolean = true,
  scheduledOnDaysOfWeek: Set<DayOfWeek> = emptySet(),
  scheduledOnDates: Set<LocalDate> = emptySet(),
  offOnDates: Set<LocalDate> = emptySet(),
  lastModificationDateTime: LocalDateTime = LocalDateTime.now(),
  lastNotificationDate: LocalDate? = null,
  alarmDurationMinutes: Long = 0L,
  soundEnabled: Boolean = false,
  vibrationEnabled: Boolean = false,
  reminderOffsetHours: Long = 0L,
  soundId: String? = null,
): AlarmModel =
  AlarmModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn,
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
    scheduledOnDates = scheduledOnDates,
    offOnDates = offOnDates,
    lastModificationDateTime = lastModificationDateTime,
    lastNotificationDate = lastNotificationDate,
    alarmDurationMinutes = alarmDurationMinutes,
    soundEnabled = soundEnabled,
    vibrationEnabled = vibrationEnabled,
    reminderOffsetHours = reminderOffsetHours,
    soundId = soundId,
  )
