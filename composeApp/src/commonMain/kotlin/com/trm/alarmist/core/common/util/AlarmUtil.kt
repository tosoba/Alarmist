package com.trm.alarmist.core.common.util

import com.trm.alarmist.db.Alarm
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

fun calculateNextFireOnDateTime(
  fireAt: LocalTime,
  scheduledOnDaysOfWeek: Collection<DayOfWeek>,
  scheduledOnDates: Collection<LocalDate>,
  offOnDates: Collection<LocalDate>,
): LocalDateTime? {
  val now = LocalDateTime.now()

  if (scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty()) {
    return when {
      fireAt < now.time -> LocalDate.now().plus(1, DateTimeUnit.DAY)
      else -> LocalDate.now()
    }.atTime(fireAt)
  }

  fun DayOfWeek.nextScheduledOnDate(): LocalDate {
    var currentDate = now.date
    while (currentDate.dayOfWeek != this) {
      currentDate = currentDate.plus(1, DateTimeUnit.DAY)
    }
    while (currentDate.atTime(fireAt) < now || currentDate in offOnDates) {
      currentDate = currentDate.plus(1, DateTimeUnit.WEEK)
    }
    return currentDate
  }

  val nextScheduledOnDayOfWeek = scheduledOnDaysOfWeek.minOfOrNull(DayOfWeek::nextScheduledOnDate)
  val nextScheduledOnDate =
    scheduledOnDates
      .run { if (offOnDates.isEmpty()) this else filter { it !in offOnDates } }
      .minOrNull()
  return when {
    nextScheduledOnDayOfWeek != null && nextScheduledOnDate != null -> {
      minOf(nextScheduledOnDayOfWeek, nextScheduledOnDate)
    }
    nextScheduledOnDayOfWeek != null -> {
      nextScheduledOnDayOfWeek
    }
    nextScheduledOnDate != null -> {
      nextScheduledOnDate
    }
    else -> {
      null
    }
  }?.atTime(fireAt)
}

const val ALARM_OFF = 0L
const val ALARM_ON = 1L

fun Alarm.nextFireOnDateTime(): LocalDateTime? =
  if (isOn == ALARM_OFF) {
    null
  } else {
    calculateNextFireOnDateTime(
      fireAt = fireAt,
      scheduledOnDaysOfWeek = parsedScheduledOnDaysOfWeek(),
      scheduledOnDates = parsedScheduledOnDates(),
      offOnDates = parsedOffOnDates(),
    )
  }

private fun Alarm?.parsedScheduledOnDaysOfWeek(): List<DayOfWeek> =
  this?.scheduledOnDaysOfWeek?.split(",")?.map { DayOfWeek(isoDayNumber = it.toInt()) }.orEmpty()

private fun Alarm?.parsedScheduledOnDates(): List<LocalDate> =
  this?.scheduledOnDates?.split(",")?.map(LocalDate.Companion::parse).orEmpty()

private fun Alarm?.parsedOffOnDates(): List<LocalDate> =
  this?.offOnDates?.split(",")?.map(LocalDate.Companion::parse).orEmpty()
