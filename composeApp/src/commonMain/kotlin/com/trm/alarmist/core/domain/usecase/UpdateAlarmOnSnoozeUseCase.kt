package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.snoozedFireAtTime
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler

class UpdateAlarmOnSnoozeUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long) {
    val alarm = repository.updateAlarmOnSnooze(id)
    snoozedFireAtTime(
        lastSnoozedAt = alarm.lastSnoozedAt,
        snoozeDurationMinutes = alarm.snoozeDurationMinutes,
      )
      ?.let {
        calculateAlarmNextFireOnDateTime(
          fireAtTime = it,
          scheduledOnDaysOfWeek = emptyList(),
          scheduledOnDates = emptyList(),
          offOnDates = emptyList(),
        )
      }
      ?.let {
        scheduler.scheduleAlarm(
          id = id,
          name = alarm.name,
          fireOnDateTime = it,
          snoozeAvailable = alarm.snoozeCount < alarm.snoozeLimit,
          alarmDurationMinutes = alarm.alarmDurationMinutes,
          soundEnabled = alarm.soundEnabled,
          soundId = alarm.soundId,
          vibrationEnabled = alarm.vibrationEnabled,
          reminderOffsetHours = alarm.reminderOffsetHours
        )
      }
  }
}
