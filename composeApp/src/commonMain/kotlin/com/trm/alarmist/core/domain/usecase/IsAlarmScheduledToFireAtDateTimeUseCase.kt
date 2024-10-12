package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.expectedOneTimeNotificationDateTime
import com.trm.alarmist.core.common.util.isCustomScheduledToFireOn
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
        alarm.snoozedFireAtDateTime == fireAtDateTime ||
          (alarm.isCustomScheduledToFireOn(fireAtDateTime.date) &&
            alarm.fireAtTime == fireAtDateTime.time)
      }
    }
  }
}
