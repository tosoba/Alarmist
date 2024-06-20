package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.usecase.calculateScheduledAlarmNextFireOnDateForDateList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.plus

class CalculateScheduledAlarmNextFireOnDateForDateListTests {
  @Test
  fun `given scheduledOnDate equal to afterDateTime date and fireAtTime before afterDateTime and empty offOnDates - then return null`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.MAY, dayOfMonth = 8),
        LocalTime(hour = 14, minute = 11, second = 8),
      )

    assertNull(
      calculateScheduledAlarmNextFireOnDateForDateList(
        scheduledOnDates = listOf(afterDateTime.date),
        fireAtTime = LocalTime(hour = 13, minute = 10, second = 18),
        offOnDates = emptyList(),
        afterDateTime = afterDateTime,
      )
    )
  }

  @Test
  fun `given scheduledOnDate equal to afterDateTime date and fireAtTime after afterDateTime and empty offOnDates - then return afterDateTime date`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.MAY, dayOfMonth = 8),
        LocalTime(hour = 14, minute = 11, second = 8),
      )

    assertEquals(
      afterDateTime.date,
      calculateScheduledAlarmNextFireOnDateForDateList(
        scheduledOnDates = listOf(afterDateTime.date),
        fireAtTime = LocalTime(hour = 16, minute = 18, second = 15),
        offOnDates = emptyList(),
        afterDateTime = afterDateTime,
      ),
    )
  }

  @Test
  fun `given multiple scheduledOnDates and fireAtTime before afterDateTime and empty offOnDates - then return earliest date after afterDateTime`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.MAY, dayOfMonth = 8),
        LocalTime(hour = 14, minute = 11, second = 8),
      )
    val scheduledOnDates = List(20) { afterDateTime.date.plus(it - 10, DateTimeUnit.DAY) }

    assertEquals(
      scheduledOnDates.filter { it > afterDateTime.date }.min(),
      calculateScheduledAlarmNextFireOnDateForDateList(
        scheduledOnDates = scheduledOnDates,
        fireAtTime = LocalTime(hour = 13, minute = 10, second = 18),
        offOnDates = emptyList(),
        afterDateTime = afterDateTime,
      ),
    )
  }

  @Test
  fun `given multiple scheduledOnDates and multiple offOnDates - then return earliest date not in offOnDates`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.MAY, dayOfMonth = 8),
        LocalTime(hour = 14, minute = 11, second = 8),
      )
    val scheduledOnDates = List(10) { afterDateTime.date.plus(it + 1, DateTimeUnit.DAY) }
    val offOnDates = scheduledOnDates.take(5)

    assertEquals(
      scheduledOnDates.filter { it !in offOnDates }.min(),
      calculateScheduledAlarmNextFireOnDateForDateList(
        scheduledOnDates = scheduledOnDates,
        fireAtTime = LocalTime(hour = 13, minute = 10, second = 18),
        offOnDates = offOnDates,
        afterDateTime = afterDateTime,
      ),
    )
  }
}
