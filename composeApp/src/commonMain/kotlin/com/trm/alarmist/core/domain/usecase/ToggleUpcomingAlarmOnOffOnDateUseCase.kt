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
    if (toggledAlarm.scheduledOnDaysOfWeek.isEmpty() && toggledAlarm.scheduledOnDates.isEmpty()) {
      calculateAlarmNextFireOnDateTime(toggledAlarm)?.let {
        scheduler.scheduleAlarm(id = id, fireOnDateTime = it)
      } ?: run { scheduler.cancelAlarm(id) }
    } else {
      calculateAlarmNextFireOnDateTime(toggledAlarm)
        ?.takeIf { it.date == date }
        ?.let {
          if (toggledAlarm.isOn) {
            scheduler.scheduleAlarm(id = id, fireOnDateTime = it)
          } else {
            scheduler.cancelAlarm(id)
          }
        }
      // scheduler.cancelAlarm(id) is not called here since a scheduled alarm might be scheduled on an
      // earlier date
    }
  }
}
