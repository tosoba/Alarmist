package com.trm.alarmist.feature.alarm.model

import androidx.compose.runtime.Immutable
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AlarmState(
  val fireAtTime: LocalTime? = null,
  val groupId: Long = AlarmGroupModel.UNGROUPED_ID,
  val name: String? = null,
  val isOn: Boolean = true,
  val scheduledOnDaysOfWeek: Set<DayOfWeek> = emptySet(),
  val scheduledOnDates: Set<LocalDate> = emptySet(),
  val offOnDates: Set<LocalDate> = emptySet(),
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
    isOn = alarm.isOn,
    scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek.toSet(),
    scheduledOnDates = alarm.scheduledOnDates.toSet(),
    offOnDates = alarm.offOnDates.toSet(),
    alarmDuration = alarm.alarmDurationMinutes,
    soundEnabled = alarm.soundEnabled,
    vibrationEnabled = alarm.vibrationEnabled,
    reminderEnabled = alarm.reminderOffsetHours > 0L,
    reminderOffset = AlarmReminderOffset.fromHours(alarm.reminderOffsetHours),
    soundId = alarm.soundId,
  )

  val reminderOffsetOrZero: Long
    get() = if (reminderEnabled) reminderOffset.hours else 0L

  companion object {
    const val MIN_ALARM_DURATION_MINUTES = 1L
    const val DEFAULT_ALARM_DURATION_MINUTES = 1L
    const val MAX_ALARM_DURATION_MINUTES = 10L
  }
}
