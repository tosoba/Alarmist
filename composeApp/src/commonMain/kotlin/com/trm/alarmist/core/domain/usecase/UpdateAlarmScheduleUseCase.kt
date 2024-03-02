package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class UpdateAlarmScheduleUseCase(private val scheduler: AlarmScheduler) {
  operator fun invoke(alarmModel: AlarmModel) {
    invoke(
      isOn = alarmModel.isOn,
      id = alarmModel.id,
      fireAtTime = alarmModel.fireAtTime,
      scheduledOnDaysOfWeek = alarmModel.scheduledOnDaysOfWeek,
      scheduledOnDates = alarmModel.scheduledOnDates,
      offOnDates = alarmModel.offOnDates,
    )
  }

  operator fun invoke(
    isOn: Boolean,
    id: Long,
    fireAtTime: LocalTime,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    if (isOn) {
      calculateAlarmNextFireOnDateTime(
          fireAtTime = fireAtTime,
          scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
          scheduledOnDates = scheduledOnDates,
          offOnDates = offOnDates,
        )
        ?.let { scheduler.scheduleAlarm(id = id, fireOnDateTime = it) }
    } else {
      scheduler.cancelAlarm(id)
    }
  }
}
