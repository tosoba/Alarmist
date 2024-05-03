package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.LocalDate

class ToggleUpcomingAlarmOnOffOnDateUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long, date: LocalDate) {
    val toggledAlarm = repository.toggleAlarmOnOffOnDate(id, date)
    if (toggledAlarm.scheduledOnDaysOfWeek.isEmpty() && toggledAlarm.scheduledOnDates.isEmpty()) {
      updateAlarmScheduleUseCase(toggledAlarm)
    } else {
      calculateAlarmNextFireOnDateTime(toggledAlarm)
        ?.takeIf { it.date == date }
        ?.let {
          if (toggledAlarm.isOn) {
            scheduler.scheduleAlarm(
              id = id,
              name = toggledAlarm.name,
              fireOnDateTime = it,
              snoozeAvailable = toggledAlarm.snoozeDurationMinutes > 0L,
              alarmDurationMinutes = toggledAlarm.alarmDurationMinutes,
              soundEnabled = toggledAlarm.soundEnabled,
              soundId = toggledAlarm.soundId,
              vibrationEnabled = toggledAlarm.vibrationEnabled,
              reminderOffsetHours = toggledAlarm.reminderOffsetHours,
            )
          } else {
            scheduler.cancelAlarm(id)
          }
        }
      // scheduler.cancelAlarm(id) is only called for a scheduled alarm if date matches
      // since a scheduled alarm might already be scheduled on an earlier date
    }
  }
}
