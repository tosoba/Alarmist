package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.data.AlarmLocalRepository
import com.trm.alarmist.core.util.addTestAlarm
import com.trm.alarmist.core.util.createTestAlarmLocalRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class GetNextTodayAlarmUseCaseTests {
  private val dispatcher = StandardTestDispatcher()

  private lateinit var repo: AlarmLocalRepository

  @BeforeTest
  fun before() {
    repo = createTestAlarmLocalRepository(dispatcher)
  }

  @Test
  fun `given no alarms - then return null`() =
    runTest(dispatcher) { assertNull(GetNextTodayAlarmUseCase(repo)(now = LocalDateTime.now())) }

  @Test
  fun `given multiple on alarms scheduled for today - then return next on alarm`() =
    runTest(dispatcher) {
      val expectedFireAtTime = LocalDateTime(LocalDate.now(), LocalTime(11, 45))
      with(repo) {
        addTestAlarm(fireAtTime = expectedFireAtTime.time, isOn = true)
        addTestAlarm(fireAtTime = LocalTime(12, 25), isOn = true)
        addTestAlarm(fireAtTime = LocalTime(12, 35), isOn = true)
        addTestAlarm(fireAtTime = LocalTime(15, 15), isOn = true)
      }

      assertEquals(
        expected = expectedFireAtTime,
        actual =
          GetNextTodayAlarmUseCase(repo)(
              now = LocalDateTime(expectedFireAtTime.date, LocalTime(11, 15, 45))
            )
            ?.fireOnDateTime,
      )
    }

  @Test
  fun `given multiple on alarms scheduled for different days - then return next on alarm scheduled for today`() =
    runTest(dispatcher) {
      val expectedFireAtTime = LocalDateTime(LocalDate.now(), LocalTime(11, 45))
      with(repo) {
        addTestAlarm(fireAtTime = LocalTime(12, 35), isOn = true)
        addTestAlarm(fireAtTime = expectedFireAtTime.time, isOn = true)
        addTestAlarm(fireAtTime = LocalTime(10, 15), isOn = true)
        addTestAlarm(
          fireAtTime = LocalTime(11, 40),
          isOn = true,
          scheduledOnDaysOfWeek =
            setOf(expectedFireAtTime.date.plus(1L, DateTimeUnit.DAY).dayOfWeek),
        )
        addTestAlarm(
          fireAtTime = LocalTime(11, 40),
          isOn = true,
          scheduledOnDates = setOf(expectedFireAtTime.date.plus(1L, DateTimeUnit.DAY)),
        )
      }

      assertEquals(
        expected = expectedFireAtTime,
        actual =
          GetNextTodayAlarmUseCase(repo)(
              now = LocalDateTime(expectedFireAtTime.date, LocalTime(11, 15, 45))
            )
            ?.fireOnDateTime,
      )
    }

  @Test
  fun `given only alarms with null fireOnDateTime - then return null`() =
    runTest(dispatcher) {
      val today = LocalDate.now()
      with(repo) {
        addTestAlarm(
          fireAtTime = LocalTime(11, 40),
          isOn = true,
          scheduledOnDaysOfWeek = setOf(today.dayOfWeek),
          offOnDates = setOf(today),
        )
        addTestAlarm(
          fireAtTime = LocalTime(11, 40),
          isOn = true,
          scheduledOnDates = setOf(today),
          offOnDates = setOf(today),
        )
      }

      assertNull(
        GetNextTodayAlarmUseCase(repo)(now = LocalDateTime(today, LocalTime(11, 15, 45)))
          ?.fireOnDateTime
      )
    }
}
