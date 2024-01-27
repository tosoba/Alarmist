package com.trm.alarmist.core.common.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

fun now(): Instant = Clock.System.now()

fun Instant.nextFullHour(): Int =
  plus(1, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault()).hour

fun LocalDateTime.Companion.now(): LocalDateTime {
  return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDate.Companion.now(): LocalDate {
  return LocalDateTime.now().date
}

fun LocalTime.Companion.now(): LocalTime {
  return LocalDateTime.now().time
}

fun LocalTime.Companion.min(): LocalTime {
  return LocalTime(0, 0)
}

fun LocalTime.Companion.max(): LocalTime {
  return LocalTime(23, 59, 59, 999999999)
}
