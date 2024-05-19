package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.LocalDate

class TurnAlarmOffOnDateUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long, date: LocalDate) {
    val preModificationAlarm = repository.turnAlarmOffOnDate(id, date)
    calculateAlarmNextFireOnDateTime(preModificationAlarm)
      ?.takeIf { it.date == date }
      ?.let { scheduler.cancelAlarm(id) }
    // scheduler.cancelAlarm(id) is only called for a scheduled alarm if date matches
    // since a scheduled alarm might already be scheduled on an earlier date.
  }
}
