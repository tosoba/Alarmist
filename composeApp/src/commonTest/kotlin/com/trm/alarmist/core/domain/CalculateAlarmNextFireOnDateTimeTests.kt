package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toLocalDate
import com.trm.alarmist.core.common.util.toLocalDateTime
import com.trm.alarmist.core.common.util.toLocalTime
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class CalculateAlarmNextFireOnDateTimeTests {
  @Test
  fun `given off alarm - then return null`() {
    assertNull(calculate(isOn = false))
  }

  @Test
  fun `given one time alarm scheduled 1 second before afterDateTime - then return next day at fireAtTime`() {
    val now = Clock.System.now()
    val fireAtTime = LocalTime(10, 50)
    val afterDateTime =
      LocalDateTime(
        date = now.toLocalDate(),
        time = LocalTime.fromSecondOfDay(fireAtTime.toSecondOfDay() + 1),
      )

    assertEquals(
      LocalDateTime(afterDateTime.date.plus(1, DateTimeUnit.DAY), fireAtTime),
      calculate(fireAtTime = fireAtTime, afterDateTime = afterDateTime),
    )
  }

  @Test
  fun `given one time alarm scheduled exactly at afterDateTime - then return next day at fireAtTime`() {
    val now = Clock.System.now()
    val fireAtTime = LocalTime(10, 50)
    val afterDateTime = LocalDateTime(date = now.toLocalDate(), time = fireAtTime)

    assertEquals(
      LocalDateTime(afterDateTime.date.plus(1, DateTimeUnit.DAY), fireAtTime),
      calculate(fireAtTime = fireAtTime, afterDateTime = afterDateTime),
    )
  }

  @Test
  fun `given one time alarm scheduled after afterDateTime - then return afterDateTime date at fireAtTime`() {
    val now = Clock.System.now()
    val afterDateTime = now.toLocalDateTime()
    val fireAtTime = (now + 1.hours).toLocalTime()

    assertEquals(
      LocalDateTime(afterDateTime.date, fireAtTime),
      calculate(fireAtTime = fireAtTime, afterDateTime = afterDateTime),
    )
  }

  private fun calculate(
    fireAtTime: LocalTime = LocalTime.now(),
    scheduledOnDaysOfWeek: Collection<DayOfWeek> = emptyList(),
    scheduledOnDates: Collection<LocalDate> = emptyList(),
    offOnDates: Collection<LocalDate> = emptyList(),
    isOn: Boolean = true,
    afterDateTime: LocalDateTime = LocalDateTime.now(),
  ): LocalDateTime? =
    calculateAlarmNextFireOnDateTime(
      fireAtTime = fireAtTime,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
      isOn = isOn,
      afterDateTime = afterDateTime,
    )
}
