package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.expectedOneTimeNotificationDateTime
import com.trm.alarmist.core.common.util.isScheduledToFireOn
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus

fun calculateMissedAlarmsDateTimes(alarms: List<AlarmModel>): Map<AlarmModel, List<LocalDateTime>> {
  val now = LocalDateTime.now()
  return alarms
    .associateWith {
      if (it.scheduledOnDaysOfWeek.isEmpty() && it.scheduledOnDates.isEmpty()) {
        val expectedNotificationDateTime = it.expectedOneTimeNotificationDateTime()
        if (
          now > expectedNotificationDateTime &&
            it.lastNotificationDate != expectedNotificationDateTime.date
        ) {
          return@associateWith listOf(expectedNotificationDateTime)
        } else {
          return@associateWith emptyList()
        }
      }

      val missedDateTimes = mutableListOf<LocalDateTime>()
      val limit =
        maxOf(
          it.lastNotificationDate?.atTime(it.fireAtTime) ?: it.lastModificationDateTime,
          it.lastModificationDateTime,
        )
      var current =
        if (now.time < it.fireAtTime) {
          now.date.minus(1, DateTimeUnit.DAY).atTime(it.fireAtTime)
        } else {
          LocalDateTime(now.date, it.fireAtTime)
        }
      while (current > limit) {
        if (it.isScheduledToFireOn(current.date)) {
          missedDateTimes.add(current)
        }
        current = current.date.minus(1, DateTimeUnit.DAY).atTime(it.fireAtTime)
      }

      missedDateTimes
    }
    .filterValues(List<LocalDateTime>::isNotEmpty)
}
