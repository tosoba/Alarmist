package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.LocalDate

class TurnAlarmOnOnDateUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long, date: LocalDate) {
    val modifiedAlarm = repository.turnAlarmOnOnDate(id, date)
    calculateAlarmNextFireOnDateTime(modifiedAlarm)
      ?.takeIf { it.date == date }
      ?.let {
        scheduler.scheduleAlarm(
          id = id,
          name = modifiedAlarm.name,
          fireOnDateTime = it,
          snoozeAvailable = modifiedAlarm.snoozeDurationMinutes > 0L,
          alarmDurationMinutes = modifiedAlarm.alarmDurationMinutes,
          soundEnabled = modifiedAlarm.soundEnabled,
          soundId = modifiedAlarm.soundId,
          vibrationEnabled = modifiedAlarm.vibrationEnabled,
          reminderOffsetHours = modifiedAlarm.reminderOffsetHours,
        )
      }
  }
}
