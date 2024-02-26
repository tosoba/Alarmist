package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class AddAlarmUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
) {
  suspend operator fun invoke(
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    val id =
      repository.addAlarm(
        fireAtTime = fireAtTime,
        name = name,
        isOn = isOn,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
        scheduledOnDates = scheduledOnDates,
        offOnDates = offOnDates,
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
