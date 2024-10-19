package com.trm.alarmist.core.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.plus

class CalculateScheduledAlarmNextFireOnDateForDaysOfWeekTests {
  @Test
  fun `given day of week equal to afterDateTime day of week and fireAtTime after afterDateTime and empty offOnDates - then return afterDateTime date`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.JUNE, dayOfMonth = 10),
        LocalTime(hour = 11, minute = 55, second = 44),
      )

    assertEquals(
      afterDateTime.date,
      calculateAlarmNextFireOnDateTime(
          fireAtTime = LocalTime(hour = 11, minute = 56),
          scheduledOnDaysOfWeek = listOf(afterDateTime.dayOfWeek),
          scheduledOnDates = emptyList(),
          offOnDates = emptyList(),
          afterDateTime = afterDateTime,
        )
        ?.date,
    )
  }

  @Test
  fun `given day of week equal to afterDateTime day of week and fireAtTime before afterDateTime and empty offOnDates - then return date 1 week after afterDateTime`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.JULY, dayOfMonth = 20),
        LocalTime(hour = 11, minute = 55, second = 1),
      )

    assertEquals(
      afterDateTime.date.plus(7L, DateTimeUnit.DAY),
      calculateAlarmNextFireOnDateTime(
          fireAtTime = LocalTime(hour = 11, minute = 55),
          scheduledOnDaysOfWeek = listOf(afterDateTime.dayOfWeek),
          scheduledOnDates = emptyList(),
          offOnDates = emptyList(),
          afterDateTime = afterDateTime,
        )
        ?.date,
    )
  }

  @Test
  fun `given day of week not equal to afterDateTime day of week and empty offOnDates - then return next appropriate day of week after afterDateTime`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.JULY, dayOfMonth = 20),
        LocalTime(hour = 11, minute = 55, second = 1),
      )

    DayOfWeek.entries
      .filter { it != afterDateTime.dayOfWeek }
      .forEach {
        val result =
          calculateAlarmNextFireOnDateTime(
            fireAtTime = LocalTime(hour = 16, minute = 55),
            scheduledOnDaysOfWeek = listOf(it),
            scheduledOnDates = emptyList(),
            offOnDates = emptyList(),
            afterDateTime = afterDateTime,
          )

        assertNotNull(result)
        assertEquals(it, result.dayOfWeek)
        assertTrue(result.date > afterDateTime.date)
      }
  }

  @Test
  fun `given day of week equal to afterDateTime day of week and fireAtTime after afterDateTime and offOnDates containing afterDateTime date - then return date 1 week after afterDateTime`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.JULY, dayOfMonth = 10),
        LocalTime(hour = 9, minute = 45, second = 12),
      )

    assertEquals(
      afterDateTime.date.plus(7L, DateTimeUnit.DAY),
      calculateAlarmNextFireOnDateTime(
          fireAtTime = LocalTime(hour = 20, minute = 20),
          scheduledOnDaysOfWeek = listOf(afterDateTime.dayOfWeek),
          scheduledOnDates = emptyList(),
          offOnDates = listOf(afterDateTime.date),
          afterDateTime = afterDateTime,
        )
        ?.date,
    )
  }

  @Test
  fun `given multiple days of week and empty offOnDates - then return the closest day after afterDateTime`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.SEPTEMBER, dayOfMonth = 21),
        LocalTime(hour = 11, minute = 24, second = 53),
      )
    val days = daysOfWeekOtherThanOf(afterDateTime)

    assertEquals(
      days.min(),
      calculateAlarmNextFireOnDateTime(
          fireAtTime = LocalTime(hour = 11, minute = 25),
          scheduledOnDaysOfWeek = days.map(LocalDate::dayOfWeek),
          scheduledOnDates = emptyList(),
          offOnDates = emptyList(),
          afterDateTime = afterDateTime,
        )
        ?.date,
    )
  }

  @Test
  fun `given multiple days of week and non empty offOnDates - then return the closest day after afterDateTime not contained in offOnDates`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.SEPTEMBER, dayOfMonth = 21),
        LocalTime(hour = 11, minute = 24, second = 53),
      )
    val days = daysOfWeekOtherThanOf(afterDateTime)
    val offOnDates = days.take(2)

    assertEquals(
      days.filter { it !in offOnDates }.min(),
      calculateAlarmNextFireOnDateTime(
          fireAtTime = LocalTime(hour = 11, minute = 25),
          scheduledOnDaysOfWeek = days.map(LocalDate::dayOfWeek),
          scheduledOnDates = emptyList(),
          offOnDates = offOnDates,
          afterDateTime = afterDateTime,
        )
        ?.date,
    )
  }

  private fun daysOfWeekOtherThanOf(afterDateTime: LocalDateTime): List<LocalDate> =
    List(DayOfWeek.entries.size - 1) { afterDateTime.date.plus(it, DateTimeUnit.DAY) }
}
