package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.usecase.calculateScheduledAlarmNextFireOnDateForDaysOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month

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
}
