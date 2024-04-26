package com.trm.alarmist.feature.alarm

import androidx.compose.runtime.Immutable
import com.trm.alarmist.core.common.util.nextFullHour
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AlarmState(
  val fireAtTime: LocalTime = LocalTime(now().nextFullHour(), 0),
  val groupId: Long = AlarmGroupModel.UNGROUPED_ID,
  val name: String? = null,
  val scheduledOnDaysOfWeek: Set<DayOfWeek> = emptySet(),
  val scheduledOnDates: Set<LocalDate> = emptySet(),
  val offOnDates: Set<LocalDate> = emptySet(),
  val snoozeEnabled: Boolean = true,
  val snoozeDuration: AlarmSnoozeDuration = AlarmSnoozeDuration.MIN_10,
  val snoozeLimit: Long = DEFAULT_SNOOZE_LIMIT,
  val alarmDuration: Long = DEFAULT_ALARM_DURATION_MINUTES,
  val soundEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true,
  val reminderEnabled: Boolean = true,
  val reminderOffset: AlarmReminderOffset = AlarmReminderOffset.HOUR_1,
  val soundId: String? = null,
) {
  constructor(
    alarm: AlarmModel
  ) : this(
    fireAtTime = alarm.fireAtTime,
    groupId = alarm.groupId ?: AlarmGroupModel.UNGROUPED_ID,
    name = alarm.name,
    scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek.toSet(),
    scheduledOnDates = alarm.scheduledOnDates.toSet(),
    offOnDates = alarm.offOnDates.toSet(),
    snoozeEnabled = alarm.snoozeDurationMinutes > 0L,
    snoozeDuration = AlarmSnoozeDuration.fromMinutes(alarm.snoozeDurationMinutes),
    snoozeLimit = alarm.snoozeLimit.takeIf { it > 0L } ?: DEFAULT_SNOOZE_LIMIT,
    alarmDuration = alarm.alarmDurationMinutes,
    soundEnabled = alarm.soundEnabled,
    vibrationEnabled = alarm.vibrationEnabled,
    reminderEnabled = alarm.reminderOffsetHours > 0L,
    reminderOffset = AlarmReminderOffset.fromHours(alarm.reminderOffsetHours),
    soundId = alarm.soundId,
  )

  constructor(
    alarm: AlarmListModel
  ) : this(
    fireAtTime = alarm.fireAtTime,
    groupId = alarm.groupId ?: AlarmGroupModel.UNGROUPED_ID,
    name = alarm.name,
  )

  val snoozeDurationOrZero: Long
    get() = if (snoozeEnabled) snoozeDuration.minutes else 0L

  val snoozeLimitOrZero: Long
    get() = if (snoozeEnabled) snoozeLimit else 0L

  val reminderOffsetOrZero: Long
    get() = if (reminderEnabled) reminderOffset.hours else 0L

  companion object {
    const val MIN_SNOOZE_LIMIT = 1L
    const val DEFAULT_SNOOZE_LIMIT = 2L
    const val MAX_SNOOZE_LIMIT = 10L

    const val MIN_ALARM_DURATION_MINUTES = 1L
    const val DEFAULT_ALARM_DURATION_MINUTES = 1L
    const val MAX_ALARM_DURATION_MINUTES = 10L
  }
}
