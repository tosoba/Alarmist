package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.expectedOneTimeNotificationDateTime
import com.trm.alarmist.core.common.util.isScheduledToFireOn
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus

fun calculateAlarmMissedDateTimes(
  alarm: AlarmModel,
  now: LocalDateTime = LocalDateTime.now(),
): List<LocalDateTime> {
  if (alarm.isOneTime) {
    val expectedNotificationDateTime = alarm.expectedOneTimeNotificationDateTime()
    return if (
      now > expectedNotificationDateTime &&
        alarm.lastNotificationDate != expectedNotificationDateTime.date
    ) {
      listOf(expectedNotificationDateTime)
    } else {
      emptyList()
    }
  }

  val missedDateTimes = mutableListOf<LocalDateTime>()
  val limit =
    maxOf(
      alarm.lastNotificationDate?.atTime(alarm.fireAtTime) ?: alarm.lastModificationDateTime,
      alarm.lastModificationDateTime,
    )
  var current =
    LocalDateTime(
      date = if (now.time < alarm.fireAtTime) now.date.minus(1, DateTimeUnit.DAY) else now.date,
      time = alarm.fireAtTime,
    )
  while (current > limit) {
    if (alarm.isScheduledToFireOn(current.date)) {
      missedDateTimes.add(current)
    }
    current = current.date.minus(1, DateTimeUnit.DAY).atTime(alarm.fireAtTime)
  }
  return missedDateTimes
}
