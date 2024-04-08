package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class EditAlarmUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
) {
  suspend operator fun invoke(
    id: Long,
    groupId: Long?,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    snoozeDurationMinutes: Long,
  ) {
    repository.editAlarm(
      id = id,
      groupId = groupId,
      fireAtTime = fireAtTime,
      name = name,
      isOn = isOn,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
      snoozeDurationMinutes = snoozeDurationMinutes,
    )
    updateAlarmScheduleUseCase(
      isOn = isOn,
      id = id,
      fireAtTime = fireAtTime,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
    )
  }
}
