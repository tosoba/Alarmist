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
): LocalDateTime? =
  when {
    !isOn -> {
      null
    }
    scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty() -> {
      calculateOneTimeAlarmNextFireOnDateTime(fireAtTime, afterDateTime)
    }
    else -> {
      calculateScheduledAlarmNextFireOnDateTime(
        fireAtTime = fireAtTime,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
        scheduledOnDates = scheduledOnDates,
        offOnDates = offOnDates,
        afterDateTime = afterDateTime,
      )
    }
  }

private fun calculateOneTimeAlarmNextFireOnDateTime(
  fireAtTime: LocalTime,
  afterDateTime: LocalDateTime,
): LocalDateTime =
  when {
    fireAtTime <= afterDateTime.time -> afterDateTime.date.plus(1, DateTimeUnit.DAY)
    else -> afterDateTime.date
  }.atTime(fireAtTime)

private fun calculateScheduledAlarmNextFireOnDateTime(
  fireAtTime: LocalTime,
  scheduledOnDaysOfWeek: Collection<DayOfWeek>,
  scheduledOnDates: Collection<LocalDate>,
  offOnDates: Collection<LocalDate>,
  afterDateTime: LocalDateTime,
): LocalDateTime? =
  listOfNotNull(
      calculateScheduledAlarmNextFireOnDateForDaysOfWeek(
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
        fireAtTime = fireAtTime,
        offOnDates = offOnDates,
        afterDateTime = afterDateTime,
      ),
      calculateScheduledAlarmNextFireOnDateForDateList(
        scheduledOnDates = scheduledOnDates,
        fireAtTime = fireAtTime,
        offOnDates = offOnDates,
        afterDateTime = afterDateTime,
      ),
    )
    .minOrNull()
    ?.atTime(fireAtTime)

private fun calculateScheduledAlarmNextFireOnDateForDaysOfWeek(
  scheduledOnDaysOfWeek: Collection<DayOfWeek>,
  fireAtTime: LocalTime,
  offOnDates: Collection<LocalDate>,
  afterDateTime: LocalDateTime,
): LocalDate? {
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

  return scheduledOnDaysOfWeek.minOfOrNull(DayOfWeek::nextScheduledOnDate)
}

private fun calculateScheduledAlarmNextFireOnDateForDateList(
  scheduledOnDates: Collection<LocalDate>,
  fireAtTime: LocalTime,
  offOnDates: Collection<LocalDate>,
  afterDateTime: LocalDateTime,
): LocalDate? =
  scheduledOnDates.filter { it.atTime(fireAtTime) > afterDateTime && it !in offOnDates }.minOrNull()
