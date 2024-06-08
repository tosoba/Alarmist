package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.LocalDate

class TurnAlarmOffOnDateUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long, date: LocalDate) {
    val modifiedAlarm = repository.turnAlarmOffOnDate(id, date)
    calculateAlarmNextFireOnDateTime(modifiedAlarm)
      ?.takeIf { it.date > date }
      ?.let {
        // If modified alarm should be scheduled for a date later than date argument
        // then alarm is rescheduled.
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
      ?: run {
        // calculate nextFireOnDateTime for pre-modified alarm
        calculateAlarmNextFireOnDateTime(
            modifiedAlarm.copy(offOnDates = modifiedAlarm.offOnDates - date)
          )
          ?.takeIf { it.date == date }
          ?.let { scheduler.cancelAlarm(id) }
        // scheduler.cancelAlarm(id) is only called for a scheduled alarm
        // if nextFireOnDateTime calculated for pre-modified alarm matches date argument
        // since a scheduled alarm might already be scheduled on an earlier date.
      }
  }
}
