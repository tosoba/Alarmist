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

fun Instant.nextFullHour(): Int =
  plus(1, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault()).hour

fun LocalDateTime.Companion.now(): LocalDateTime =
  Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDate.Companion.now(): LocalDate = LocalDateTime.now().date

fun LocalTime.Companion.now(): LocalTime = LocalDateTime.now().time

fun Instant.toLocalDateTimeDefault(): LocalDateTime =
  toLocalDateTime(TimeZone.currentSystemDefault())

fun Instant.toLocalTimeDefault(): LocalTime = toLocalDateTimeDefault().time

fun Instant.toLocalDateDefault(): LocalDate = toLocalDateTimeDefault().date

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
fun LocalTime.toFormattedString(): String {
  val is24HoursFormat = is24HoursFormat()
  return format(
    LocalTime.Format {
      if (!is24HoursFormat) amPmHour(padding = Padding.ZERO) else hour(padding = Padding.ZERO)
      char(':')
      minute(padding = Padding.ZERO)
    }
  )
}

@Composable
fun LocalTime.amPmString(): String {
  val is24HoursFormat = is24HoursFormat()
  return format(LocalTime.Format { if (!is24HoursFormat) amPmMarker("AM", "PM") })
}
