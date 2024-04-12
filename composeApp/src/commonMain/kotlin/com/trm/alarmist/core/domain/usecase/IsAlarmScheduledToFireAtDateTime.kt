package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.expectedOneTimeNotificationDateTime
import com.trm.alarmist.core.common.util.isScheduledToFireOn
import com.trm.alarmist.core.common.util.snoozedFireAtTime
import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.datetime.LocalDateTime

class IsAlarmScheduledToFireAtDateTime(private val repository: AlarmRepository) {
  suspend operator fun invoke(id: Long, fireAtDateTime: LocalDateTime): Boolean {
    val alarm = repository.getAlarmById(id)
    return if (alarm.scheduledOnDaysOfWeek.isEmpty() && alarm.scheduledOnDates.isEmpty()) {
      fireAtDateTime == alarm.expectedOneTimeNotificationDateTime()
    } else {
      alarm.isScheduledToFireOn(fireAtDateTime.date) &&
        (snoozedFireAtTime(
          lastSnoozedAt = alarm.lastSnoozedAt,
          snoozeDurationMinutes = alarm.snoozeDurationMinutes,
          snoozeCount = alarm.snoozeCount,
        ) ?: alarm.fireAtTime) == fireAtDateTime.time
    }
  }
}
