package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.usecase.calculateScheduledAlarmNextFireOnDateForDaysOfWeek
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
      calculateScheduledAlarmNextFireOnDateForDaysOfWeek(
        scheduledOnDaysOfWeek = listOf(afterDateTime.dayOfWeek),
        fireAtTime = LocalTime(hour = 11, minute = 56),
        offOnDates = emptyList(),
        afterDateTime = afterDateTime,
      ),
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
      calculateScheduledAlarmNextFireOnDateForDaysOfWeek(
        scheduledOnDaysOfWeek = listOf(afterDateTime.dayOfWeek),
        fireAtTime = LocalTime(hour = 11, minute = 55),
        offOnDates = emptyList(),
        afterDateTime = afterDateTime,
      ),
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
          calculateScheduledAlarmNextFireOnDateForDaysOfWeek(
            scheduledOnDaysOfWeek = listOf(it),
            fireAtTime = LocalTime(hour = 16, minute = 55),
            offOnDates = emptyList(),
            afterDateTime = afterDateTime,
          )

        assertNotNull(result)
        assertEquals(it, result.dayOfWeek)
        assertTrue(result > afterDateTime.date)
      }
  }
}
