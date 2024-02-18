package com.trm.alarmist.core.common.util

import kotlin.time.Duration
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

fun LocalDateTime.Companion.now(): LocalDateTime =
  Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDate.Companion.now(): LocalDate = LocalDateTime.now().date

fun LocalTime.Companion.now(): LocalTime = LocalDateTime.now().time

fun LocalTime.Companion.min(): LocalTime = LocalTime(0, 0)

fun LocalTime.Companion.max(): LocalTime = LocalTime(23, 59, 59, 999999999)

fun Duration.formatCountdown(): String {
  fun Int.withZeroPrefix() = if (this < 10) "0$this" else this.toString()

  return when {
    inWholeDays > 1L -> {
      "$inWholeDays days"
    }
    inWholeDays == 1L -> {
      "1 day"
    }
    else -> {
      toComponents { hours, minutes, seconds, _ ->
        "$hours:${minutes.withZeroPrefix()}:${seconds.withZeroPrefix()}"
      }
    }
  }
}
