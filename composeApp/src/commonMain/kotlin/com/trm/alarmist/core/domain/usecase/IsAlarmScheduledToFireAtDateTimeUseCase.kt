package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.expectedOneTimeNotificationDateTime
import com.trm.alarmist.core.common.util.isScheduledToFireOn
import com.trm.alarmist.core.common.util.snoozedFireAtTime
import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.datetime.LocalDateTime

class IsAlarmScheduledToFireAtDateTimeUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(id: Long, fireAtDateTime: LocalDateTime): Boolean {
    val alarm = repository.getAlarmById(id)
    return when {
      !alarm.isOn -> {
        false
      }
      alarm.isOneTime -> {
        fireAtDateTime == alarm.expectedOneTimeNotificationDateTime()
      }
      else -> {
        alarm.isScheduledToFireOn(fireAtDateTime.date) &&
          (snoozedFireAtTime(
            lastSnoozedAt = alarm.lastSnoozedAt,
            snoozeDurationMinutes = alarm.snoozeDurationMinutes,
          ) ?: alarm.fireAtTime) == fireAtDateTime.time
      }
    }
  }
}
