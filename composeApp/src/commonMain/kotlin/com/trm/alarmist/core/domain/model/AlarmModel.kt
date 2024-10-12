package com.trm.alarmist.core.domain.model

import com.trm.alarmist.core.common.util.toLocalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
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

  val snoozedFireAtTime: LocalTime?
    get() =
      if (lastSnoozedAt != null && snoozeDurationMinutes > 0L) {
        lastSnoozedAt
          .toInstant(TimeZone.currentSystemDefault())
          .plus(snoozeDurationMinutes, DateTimeUnit.MINUTE)
          .toLocalTime()
      } else {
        null
      }

  val nextFireAtTime: LocalTime
    get() = snoozedFireAtTime ?: fireAtTime
}
