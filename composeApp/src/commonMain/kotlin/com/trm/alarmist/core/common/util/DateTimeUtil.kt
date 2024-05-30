package com.trm.alarmist.core.common.util

import androidx.compose.runtime.Composable
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

fun now(): Instant = Clock.System.now()

fun Instant.nextFullHour(timeZone: TimeZone = TimeZone.currentSystemDefault()): Int =
  plus(1, DateTimeUnit.HOUR).toLocalDateTime(timeZone).hour

fun LocalDateTime.Companion.now(
  timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)

fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
  LocalDateTime.now(timeZone).date

fun LocalTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime =
  LocalDateTime.now(timeZone).time

fun Instant.toLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime =
  this@toLocalDateTime.toLocalDateTime(timeZone)

fun Instant.toLocalTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime =
  toLocalDateTime(timeZone).time

fun Instant.toLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
  toLocalDateTime(timeZone).date

fun Duration.formatCountdown(): String {
  fun Int.withZeroPrefix() = if (this < 10) "0$this" else this.toString()

  return when {
    inWholeDays > 1L -> {
      "$inWholeDays days"
    }
    inWholeDays == 1L -> {
      "1 day"
    }
    inWholeHours >= 1L -> {
      toComponents { hours, minutes, _, _ -> "${hours}h ${minutes.withZeroPrefix()}m" }
    }
    inWholeMinutes >= 10L -> {
      toComponents { _, minutes, _, _ -> "${minutes}m" }
    }
    else -> {
      toComponents { _, minutes, seconds, _ ->
        "${minutes.withZeroPrefix()}:${seconds.withZeroPrefix()}"
      }
    }
  }
}

fun LocalDate.previousDayOfWeek(dayOfWeek: DayOfWeek): LocalDate {
  var current = this
  while (current.dayOfWeek != dayOfWeek) {
    current = current.minus(1, DateTimeUnit.DAY)
  }
  return current
}

fun LocalDate.nextDayOfWeek(dayOfWeek: DayOfWeek): LocalDate {
  var current = this
  while (current.dayOfWeek != dayOfWeek) {
    current = current.plus(1, DateTimeUnit.DAY)
  }
  return current
}

@Composable expect fun is24HoursFormat(): Boolean

@Composable
fun LocalTime.toFormattedString(
  using24HoursFormat: @Composable () -> Boolean = { is24HoursFormat() }
): String {
  val use24Hours = using24HoursFormat()
  return format(
    LocalTime.Format {
      if (!use24Hours) amPmHour(padding = Padding.ZERO) else hour(padding = Padding.ZERO)
      char(':')
      minute(padding = Padding.ZERO)
    }
  )
}

@Composable
fun LocalTime.amPmString(
  using24HoursFormat: @Composable () -> Boolean = { is24HoursFormat() }
): String {
  val use24Hours = using24HoursFormat()
  return format(LocalTime.Format { if (!use24Hours) amPmMarker("AM", "PM") })
}
