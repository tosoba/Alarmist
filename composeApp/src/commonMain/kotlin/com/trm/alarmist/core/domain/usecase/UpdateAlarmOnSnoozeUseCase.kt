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
    calculateAlarmNextFireOnDateTime(
        fireAtTime =
          snoozedFireAtTime(
            fireAtTime = alarm.fireAtTime,
            snoozeDurationMinutes = alarm.snoozeDurationMinutes,
            snoozeCount = alarm.snoozeCount,
          ) ?: alarm.fireAtTime,
        scheduledOnDaysOfWeek = emptyList(),
        scheduledOnDates = emptyList(),
        offOnDates = emptyList(),
      )
      ?.let { scheduler.scheduleAlarm(id, it) }
  }
}
