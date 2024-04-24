package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class EditAlarmUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
) {
  suspend operator fun invoke(
    id: Long,
    groupId: Long,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    snoozeDurationMinutes: Long,
    snoozeLimit: Long,
    ringDurationMinutes: Long,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
  ) {
    repository.editAlarm(
      id = id,
      groupId = groupId.takeIf { it != AlarmGroupModel.UNGROUPED_ID },
      fireAtTime = fireAtTime,
      name = name,
      isOn = isOn,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
      snoozeDurationMinutes = snoozeDurationMinutes,
      snoozeLimit = snoozeLimit,
      ringDurationMinutes = ringDurationMinutes,
      soundEnabled = soundEnabled,
      vibrationEnabled = vibrationEnabled,
      reminderOffsetHours = reminderOffsetHours,
    )
    updateAlarmScheduleUseCase(
      isOn = isOn,
      id = id,
      fireAtTime = fireAtTime,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
      snoozeAvailable = snoozeDurationMinutes > 0L,
      ringDurationMinutes = ringDurationMinutes,
      soundEnabled = soundEnabled,
      vibrationEnabled = vibrationEnabled,
      reminderOffsetHours = reminderOffsetHours,
    )
  }
}
