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
  calculateOneTime: CalculateOneTimeAlarmNextFireOnDateTime =
    ::calculateOneTimeAlarmNextFireOnDateTime,
  calculateScheduled: CalculateScheduledAlarmNextFireOnDateTime =
    ::calculateScheduledAlarmNextFireOnDateTime,
): LocalDateTime? =
  when {
    !isOn -> {
      null
    }
    scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty() -> {
      calculateOneTime(fireAtTime, afterDateTime)
    }
    else -> {
      calculateScheduled(
        fireAtTime,
        scheduledOnDaysOfWeek,
        scheduledOnDates,
        offOnDates,
        afterDateTime,
        ::calculateScheduledAlarmNextFireOnDateForDaysOfWeek,
        ::calculateScheduledAlarmNextFireOnDateForDateList,
      )
    }
  }

private typealias CalculateOneTimeAlarmNextFireOnDateTime =
  (LocalTime, LocalDateTime) -> LocalDateTime

internal fun calculateOneTimeAlarmNextFireOnDateTime(
  fireAtTime: LocalTime,
  afterDateTime: LocalDateTime,
): LocalDateTime =
  when {
    fireAtTime <= LocalTime(hour = afterDateTime.time.hour, minute = afterDateTime.time.minute) -> {
      afterDateTime.date.plus(1, DateTimeUnit.DAY)
    }
    else -> {
      afterDateTime.date
    }
  }.atTime(fireAtTime)

private typealias CalculateScheduledAlarmNextFireOnDateTime =
  (
    fireAtTime: LocalTime,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    afterDateTime: LocalDateTime,
    calculateForDaysOfWeek: CalculateScheduledAlarmNextFireOnDateForDaysOfWeek,
    calculateForDateList: CalculateScheduledAlarmNextFireOnDateForDateList,
  ) -> LocalDateTime?

internal fun calculateScheduledAlarmNextFireOnDateTime(
  fireAtTime: LocalTime,
  scheduledOnDaysOfWeek: Collection<DayOfWeek>,
  scheduledOnDates: Collection<LocalDate>,
  offOnDates: Collection<LocalDate>,
  afterDateTime: LocalDateTime,
  calculateForDaysOfWeek: CalculateScheduledAlarmNextFireOnDateForDaysOfWeek =
    ::calculateScheduledAlarmNextFireOnDateForDaysOfWeek,
  calculateForDateList: CalculateScheduledAlarmNextFireOnDateForDateList =
    ::calculateScheduledAlarmNextFireOnDateForDateList,
): LocalDateTime? =
  listOfNotNull(
      calculateForDaysOfWeek(scheduledOnDaysOfWeek, fireAtTime, offOnDates, afterDateTime),
      calculateForDateList(scheduledOnDates, fireAtTime, offOnDates, afterDateTime),
    )
    .minOrNull()
    ?.atTime(fireAtTime)

private typealias CalculateScheduledAlarmNextFireOnDateForDaysOfWeek =
  (Collection<DayOfWeek>, LocalTime, Collection<LocalDate>, LocalDateTime) -> LocalDate?

internal fun calculateScheduledAlarmNextFireOnDateForDaysOfWeek(
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

private typealias CalculateScheduledAlarmNextFireOnDateForDateList =
  (Collection<LocalDate>, LocalTime, Collection<LocalDate>, LocalDateTime) -> LocalDate?

internal fun calculateScheduledAlarmNextFireOnDateForDateList(
  scheduledOnDates: Collection<LocalDate>,
  fireAtTime: LocalTime,
  offOnDates: Collection<LocalDate>,
  afterDateTime: LocalDateTime,
): LocalDate? =
  scheduledOnDates.filter { it.atTime(fireAtTime) > afterDateTime && it !in offOnDates }.minOrNull()
