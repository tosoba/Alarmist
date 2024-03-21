package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.LocalDate

class ToggleUpcomingAlarmOnOffOnDateUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long, date: LocalDate) {
    val toggledAlarm = repository.toggleAlarmOnOffOnDate(id, date)
    calculateAlarmNextFireOnDateTime(toggledAlarm)
      ?.takeIf { it.date == date }
      ?.let {
        if (toggledAlarm.isOn) {
          scheduler.scheduleAlarm(id = id, fireOnDateTime = it)
        } else {
          scheduler.cancelAlarm(id)
        }
      }
  }
}
