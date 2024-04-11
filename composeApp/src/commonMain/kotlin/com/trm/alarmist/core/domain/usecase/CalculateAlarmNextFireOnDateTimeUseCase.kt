package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

fun calculateAlarmNextFireOnDateTime(alarm: AlarmModel): LocalDateTime? =
  calculateAlarmNextFireOnDateTime(
    fireAtTime = alarm.fireAtTime,
    scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek,
    scheduledOnDates = alarm.scheduledOnDates,
    offOnDates = alarm.offOnDates,
    isOn = alarm.isOn,
  )

fun calculateAlarmNextFireOnDateTime(
  fireAtTime: LocalTime,
  scheduledOnDaysOfWeek: Collection<DayOfWeek>,
  scheduledOnDates: Collection<LocalDate>,
  offOnDates: Collection<LocalDate>,
  isOn: Boolean = true,
  afterDateTime: LocalDateTime = LocalDateTime.now(),
): LocalDateTime? {
  if (!isOn) return null

  if (scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty()) {
    return when {
      fireAtTime <=
        LocalTime(hour = afterDateTime.time.hour, minute = afterDateTime.time.minute) -> {
        afterDateTime.date.plus(1, DateTimeUnit.DAY)
      }
      else -> {
        afterDateTime.date
      }
    }.atTime(fireAtTime)
  }

  fun DayOfWeek.nextScheduledOnDate(): LocalDate {
    var currentDate = afterDateTime.date
    while (currentDate.dayOfWeek != this) {
      currentDate = currentDate.plus(1, DateTimeUnit.DAY)
    }
    while (currentDate.atTime(fireAtTime) <= afterDateTime || currentDate in offOnDates) {
      currentDate = currentDate.plus(1, DateTimeUnit.WEEK)
    }
    return currentDate
  }

  return listOfNotNull(
      scheduledOnDaysOfWeek.minOfOrNull(DayOfWeek::nextScheduledOnDate),
      scheduledOnDates
        .filter { it.atTime(fireAtTime) > afterDateTime && it !in offOnDates }
        .minOrNull(),
    )
    .minOrNull()
    ?.atTime(fireAtTime)
}
