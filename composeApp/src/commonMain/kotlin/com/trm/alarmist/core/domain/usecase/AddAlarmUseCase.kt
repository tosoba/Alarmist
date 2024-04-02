package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class AddAlarmUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
) {
  suspend operator fun invoke(
    groupId: Long,
    name: String?,
    fireAtTime: LocalTime,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    updateAlarmScheduleUseCase(
      isOn = isOn,
      id =
        repository.addAlarm(
          groupId = groupId.takeIf { it != AlarmGroupModel.UNGROUPED_ID },
          fireAtTime = fireAtTime,
          name = name,
          isOn = isOn,
          scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
          scheduledOnDates = scheduledOnDates,
          offOnDates = offOnDates,
        ),
      fireAtTime = fireAtTime,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
    )
  }
}
