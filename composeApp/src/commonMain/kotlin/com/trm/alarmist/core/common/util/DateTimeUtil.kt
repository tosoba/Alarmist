package com.trm.alarmist.core.common.util

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.days_count
import alarmist.composeapp.generated.resources.one_day
import androidx.compose.runtime.Composable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
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
import org.jetbrains.compose.resources.stringResource

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

fun Int.zeroPadded(): String = toString().padStart(2, '0')

@Composable
fun Duration.formatCountdown(): String =
  when {
    inWholeDays > 1L -> {
      stringResource(Res.string.days_count, inWholeDays)
    }
    inWholeDays == 1L -> {
      stringResource(Res.string.one_day)
    }
    inWholeHours >= 1L -> {
      toComponents { hours, minutes, _, _ -> "${hours}h ${minutes.zeroPadded()}m" }
    }
    inWholeMinutes >= 10L -> {
      toComponents { _, minutes, _, _ -> "${minutes}m" }
    }
    else -> {
      toComponents { _, minutes, seconds, _ -> "${minutes.zeroPadded()}:${seconds.zeroPadded()}" }
    }
  }

@Composable
fun Duration.formatHMS(): String = toComponents { hours, minutes, seconds, _ ->
  when {
    hours > 0L -> "${hours}h ${minutes}m ${seconds}s"
    minutes > 0L -> "${minutes}m ${seconds}s"
    else -> "${seconds}s"
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

fun Duration.toNotificationFormat(): String =
  if (inWholeMilliseconds % 1_000L != 0L) {
      plus(1.seconds)
    } else {
      this
    }
    .toComponents { hours, minutes, seconds, _ ->
      "${hours.toInt().zeroPadded()}:${minutes.zeroPadded()}:${seconds.zeroPadded()}"
    }
