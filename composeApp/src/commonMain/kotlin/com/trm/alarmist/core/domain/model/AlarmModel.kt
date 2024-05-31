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
  val scheduledOnDaysOfWeek: List<DayOfWeek>,
  val scheduledOnDates: List<LocalDate>,
  val offOnDates: List<LocalDate>,
  val lastModificationDateTime: LocalDateTime,
  val lastNotificationDate: LocalDate?,
  val snoozeDurationMinutes: Long,
  val snoozeCount: Long,
  val snoozeLimit: Long,
  val lastSnoozedAt: LocalDateTime?,
  val alarmDurationMinutes: Long,
  val soundEnabled: Boolean,
  val vibrationEnabled: Boolean,
  val reminderOffsetHours: Long,
  val soundId: String?,
) {
  val isOneTime: Boolean
    get() = scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty()
}
