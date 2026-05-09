package com.trm.alarmist.core.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class AlarmModel(
  val id: Long,
  val groupId: Long?,
  val fireAtTime: LocalTime,
  val name: String?,
  val isOn: Boolean,
  val scheduledOnDaysOfWeek: Set<DayOfWeek>,
  val scheduledOnDates: Set<LocalDate>,
  val offOnDates: Set<LocalDate>,
  val lastModificationDateTime: LocalDateTime,
  val lastNotificationDate: LocalDate?,
  val alarmDurationMinutes: Long,
  val soundEnabled: Boolean,
  val vibrationEnabled: Boolean,
  val reminderOffsetHours: Long,
  val soundId: String?,
) {
  val isOneTime: Boolean
    get() = scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty()
}
