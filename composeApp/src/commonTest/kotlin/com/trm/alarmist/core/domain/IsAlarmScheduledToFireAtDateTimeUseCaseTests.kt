package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTimeUseCase
import com.trm.alarmist.core.util.alarmModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class IsAlarmScheduledToFireAtDateTimeUseCaseTests {
  @Test
  fun `given off alarm - then return false`() = runTest {
    assertFalse(
      IsAlarmScheduledToFireAtDateTimeUseCase(
        mock { everySuspend { getAlarmById(any()) } returns alarmModel(isOn = false) }
      )(1L, LocalDateTime.now())
    )
  }

  @Test
  fun `given on one time alarm and fireAtDateTime with non matching date - then return false`() =
    runTest {
      val fireAtTime = LocalTime(8, 30)
      val lastModificationDateTime = LocalDateTime(2024, 8, 25, 7, 30)

      assertFalse(
        IsAlarmScheduledToFireAtDateTimeUseCase(
          mock {
            everySuspend { getAlarmById(any()) } returns
              alarmModel(
                isOn = true,
                fireAtTime = fireAtTime,
                lastModificationDateTime = lastModificationDateTime,
              )
          }
        )(1L, LocalDateTime(lastModificationDateTime.date.plus(1L, DateTimeUnit.DAY), fireAtTime))
      )
    }

  @Test
  fun `given on one time alarm and fireAtDateTime with non matching time - then return false`() =
    runTest {
      val fireAtTime = LocalTime(8, 30)
      val lastModificationDateTime = LocalDateTime(2024, 8, 25, 7, 30)

      assertFalse(
        IsAlarmScheduledToFireAtDateTimeUseCase(
          mock {
            everySuspend { getAlarmById(any()) } returns
              alarmModel(
                isOn = true,
                fireAtTime = fireAtTime,
                lastModificationDateTime = lastModificationDateTime,
              )
          }
        )(1L, LocalDateTime(lastModificationDateTime.date, LocalTime(8, 20)))
      )
    }

  @Test
  fun `given on one time alarm and matching fireAtDateTime - then return true`() = runTest {
    val fireAtTime = LocalTime(8, 30)
    val lastModificationDateTime = LocalDateTime(2024, 8, 25, 7, 30)

    assertTrue(
      IsAlarmScheduledToFireAtDateTimeUseCase(
        mock {
          everySuspend { getAlarmById(any()) } returns
            alarmModel(
              isOn = true,
              fireAtTime = fireAtTime,
              lastModificationDateTime = lastModificationDateTime,
            )
        }
      )(1L, LocalDateTime(lastModificationDateTime.date, fireAtTime))
    )
  }

  @Test
  fun `given on custom scheduled alarm and fireAtDateTime with non matching date - then return false`() =
    runTest {
      assertFalse(
        IsAlarmScheduledToFireAtDateTimeUseCase(
          mock {
            everySuspend { getAlarmById(any()) } returns
              alarmModel(isOn = true, scheduledOnDates = setOf(LocalDate(2024, 8, 24)))
          }
        )(1L, LocalDateTime(LocalDate(2024, 8, 25), LocalTime(8, 20)))
      )
    }

  @Test
  fun `given on custom scheduled alarm and fireAtDateTime with non matching time - then return false`() =
    runTest {
      val lastModificationDateTime = LocalDateTime(2024, 8, 25, 7, 30)

      assertFalse(
        IsAlarmScheduledToFireAtDateTimeUseCase(
          mock {
            everySuspend { getAlarmById(any()) } returns
              alarmModel(
                isOn = true,
                fireAtTime = LocalTime(9, 30),
                lastModificationDateTime = lastModificationDateTime,
                scheduledOnDates = setOf(lastModificationDateTime.date),
              )
          }
        )(1L, LocalDateTime(lastModificationDateTime.date, LocalTime(8, 20)))
      )
    }

  @Test
  fun `given on custom scheduled alarm and matching fireAtDateTime - then return true`() = runTest {
    val fireAtTime = LocalTime(9, 30)
    val lastModificationDateTime = LocalDateTime(2024, 8, 25, 7, 30)

    assertTrue(
      IsAlarmScheduledToFireAtDateTimeUseCase(
        mock {
          everySuspend { getAlarmById(any()) } returns
            alarmModel(
              isOn = true,
              fireAtTime = fireAtTime,
              lastModificationDateTime = lastModificationDateTime,
              scheduledOnDates = setOf(lastModificationDateTime.date),
            )
        }
      )(1L, LocalDateTime(lastModificationDateTime.date, fireAtTime))
    )
  }

  @Test
  fun `given on custom scheduled alarm that was snoozed and fireAtDateTime with non matching time - then return false`() =
    runTest {
      val fireAtTime = LocalTime(9, 30)
      val lastModificationDateTime = LocalDateTime(2024, 8, 25, 7, 30)

      assertFalse(
        IsAlarmScheduledToFireAtDateTimeUseCase(
          mock {
            everySuspend { getAlarmById(any()) } returns
              alarmModel(
                isOn = true,
                fireAtTime = fireAtTime,
                lastModificationDateTime = lastModificationDateTime,
                scheduledOnDates = setOf(lastModificationDateTime.date),
                lastSnoozedAt = LocalDateTime(lastModificationDateTime.date, fireAtTime),
                snoozeDurationMinutes = 5L,
              )
          }
        )(1L, LocalDateTime(lastModificationDateTime.date, fireAtTime))
      )
    }

  @Test
  fun `given on custom scheduled alarm that was snoozed and fireAtDateTime with matching time - then return true`() =
    runTest {
      val fireAtTime = LocalTime(9, 30)
      val lastModificationDateTime = LocalDateTime(2024, 8, 25, 7, 30)
      val snoozeDurationMinutes = 5L

      assertTrue(
        IsAlarmScheduledToFireAtDateTimeUseCase(
          mock {
            everySuspend { getAlarmById(any()) } returns
              alarmModel(
                isOn = true,
                fireAtTime = fireAtTime,
                lastModificationDateTime = lastModificationDateTime,
                scheduledOnDates = setOf(lastModificationDateTime.date),
                lastSnoozedAt = LocalDateTime(lastModificationDateTime.date, fireAtTime),
                snoozeDurationMinutes = snoozeDurationMinutes,
              )
          }
        )(
          1L,
          LocalDateTime(
            lastModificationDateTime.date,
            LocalTime(fireAtTime.hour, fireAtTime.minute + snoozeDurationMinutes.toInt()),
          ),
        )
      )
    }
}
