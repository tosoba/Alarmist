package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class UpdateAlarmScheduleUseCase(private val scheduler: AlarmScheduler) {
  operator fun invoke(alarmModel: AlarmModel, afterDateTime: LocalDateTime = LocalDateTime.now()) {
    invoke(
      isOn = alarmModel.isOn,
      id = alarmModel.id,
      fireAtTime = alarmModel.fireAtTime,
      scheduledOnDaysOfWeek = alarmModel.scheduledOnDaysOfWeek,
      scheduledOnDates = alarmModel.scheduledOnDates,
      offOnDates = alarmModel.offOnDates,
      afterDateTime = afterDateTime,
    )
  }

  operator fun invoke(
    isOn: Boolean,
    id: Long,
    fireAtTime: LocalTime,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    afterDateTime: LocalDateTime = LocalDateTime.now(),
  ) {
    if (isOn) {
      calculateAlarmNextFireOnDateTime(
          fireAtTime = fireAtTime,
          scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
          scheduledOnDates = scheduledOnDates,
          offOnDates = offOnDates,
          afterDateTime = afterDateTime,
        )
        ?.let { scheduler.scheduleAlarm(id = id, fireOnDateTime = it) }
    } else {
      scheduler.cancelAlarm(id)
    }
  }
}
