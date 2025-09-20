package com.trm.alarmist.core.domain.usecase

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
        LocalDate(year = 2024, month = Month.MAY, day = 8),
        LocalTime(hour = 14, minute = 11, second = 8),
      )

    assertNull(
      calculateAlarmNextFireOnDateTime(
        fireAtTime = LocalTime(hour = 13, minute = 10),
        scheduledOnDates = listOf(afterDateTime.date),
        scheduledOnDaysOfWeek = emptyList(),
        offOnDates = emptyList(),
        afterDateTime = afterDateTime,
      )
    )
  }

  @Test
  fun `given scheduledOnDate equal to afterDateTime date and fireAtTime after afterDateTime and empty offOnDates - then return afterDateTime date`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.MAY, day = 8),
        LocalTime(hour = 14, minute = 11, second = 8),
      )

    assertEquals(
      afterDateTime.date,
      calculateAlarmNextFireOnDateTime(
          fireAtTime = LocalTime(hour = 16, minute = 18),
          scheduledOnDaysOfWeek = emptyList(),
          scheduledOnDates = listOf(afterDateTime.date),
          offOnDates = emptyList(),
          afterDateTime = afterDateTime,
        )
        ?.date,
    )
  }

  @Test
  fun `given multiple scheduledOnDates and fireAtTime before afterDateTime and empty offOnDates - then return earliest date after afterDateTime`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.MAY, day = 8),
        LocalTime(hour = 14, minute = 11, second = 8),
      )
    val scheduledOnDates = List(20) { afterDateTime.date.plus(it - 10, DateTimeUnit.DAY) }

    assertEquals(
      scheduledOnDates.filter { it > afterDateTime.date }.min(),
      calculateAlarmNextFireOnDateTime(
          fireAtTime = LocalTime(hour = 13, minute = 10),
          scheduledOnDaysOfWeek = emptyList(),
          scheduledOnDates = scheduledOnDates,
          offOnDates = emptyList(),
          afterDateTime = afterDateTime,
        )
        ?.date,
    )
  }

  @Test
  fun `given multiple scheduledOnDates and multiple offOnDates - then return earliest date not in offOnDates`() {
    val afterDateTime =
      LocalDateTime(
        LocalDate(year = 2024, month = Month.MAY, day = 8),
        LocalTime(hour = 14, minute = 11, second = 8),
      )
    val scheduledOnDates = List(10) { afterDateTime.date.plus(it + 1, DateTimeUnit.DAY) }
    val offOnDates = scheduledOnDates.take(5)

    assertEquals(
      scheduledOnDates.filter { it !in offOnDates }.min(),
      calculateAlarmNextFireOnDateTime(
          fireAtTime = LocalTime(hour = 13, minute = 10),
          scheduledOnDaysOfWeek = emptyList(),
          scheduledOnDates = scheduledOnDates,
          offOnDates = offOnDates,
          afterDateTime = afterDateTime,
        )
        ?.date,
    )
  }
}
