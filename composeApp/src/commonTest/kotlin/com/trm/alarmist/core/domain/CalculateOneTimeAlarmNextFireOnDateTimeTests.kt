package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.usecase.calculateOneTimeAlarmNextFireOnDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class CalculateOneTimeAlarmNextFireOnDateTimeTests {
  @Test
  fun `given one time alarm with fireAtTime 1 hour before afterDateTime - then return next day after afterDateTime at fireAtTime`() {
    val fireAtTime = LocalTime(hour = 10, minute = 50)
    val afterDateTime =
      LocalDateTime(
        date = LocalDate.now(),
        time = LocalTime(hour = fireAtTime.hour + 1, minute = fireAtTime.minute),
      )

    assertEquals(
      LocalDateTime(date = afterDateTime.date.plus(1, DateTimeUnit.DAY), time = fireAtTime),
      calculate(fireAtTime = fireAtTime, afterDateTime = afterDateTime),
    )
  }

  @Test
  fun `given one time alarm with fireAtTime 1 second before afterDateTime - then return next day after afterDateTime at fireAtTime`() {
    val fireAtTime = LocalTime(hour = 10, minute = 50)
    val afterDateTime =
      LocalDateTime(
        date = LocalDate.now(),
        time = LocalTime(hour = fireAtTime.hour, minute = fireAtTime.minute, second = 1),
      )

    assertEquals(
      LocalDateTime(date = afterDateTime.date.plus(1, DateTimeUnit.DAY), time = fireAtTime),
      calculate(fireAtTime = fireAtTime, afterDateTime = afterDateTime),
    )
  }

  @Test
  fun `given one time alarm with fireAtTime equal to afterDateTime - then return next day after afterDateTime at fireAtTime`() {
    val fireAtTime = LocalTime(hour = 10, minute = 50)
    val afterDateTime = LocalDateTime(date = LocalDate.now(), time = fireAtTime)

    assertEquals(
      LocalDateTime(date = afterDateTime.date.plus(1, DateTimeUnit.DAY), time = fireAtTime),
      calculate(fireAtTime = fireAtTime, afterDateTime = afterDateTime),
    )
  }

  @Test
  fun `given one time alarm with fireAtTime 1 hour after afterDateTime - then return afterDateTime date at fireAtTime`() {
    val afterDateTime =
      LocalDateTime(date = LocalDate.now(), time = LocalTime(hour = 10, minute = 50))
    val fireAtTime = LocalTime(hour = afterDateTime.time.hour + 1, minute = afterDateTime.minute)

    assertEquals(
      LocalDateTime(date = afterDateTime.date, time = fireAtTime),
      calculate(fireAtTime = fireAtTime, afterDateTime = afterDateTime),
    )
  }

  private fun calculate(
    fireAtTime: LocalTime = LocalTime.now(),
    afterDateTime: LocalDateTime = LocalDateTime.now(),
  ): LocalDateTime =
    calculateOneTimeAlarmNextFireOnDateTime(fireAtTime = fireAtTime, afterDateTime = afterDateTime)
}
