package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.util.alarmModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class GetNextTodayAlarmUseCaseTests {
  @Test
  fun `given no alarms - then return null`() = runTest {
    assertNull(
      GetNextTodayAlarmUseCase(alarmRepository(onAlarms = emptyList()))(now = LocalDateTime.now())
    )
  }

  @Test
  fun `given multiple on alarms scheduled for today - then return next on alarm`() = runTest {
    val expectedFireAtTime = LocalDateTime(LocalDate.now(), LocalTime(11, 45))
    assertEquals(
      expected = expectedFireAtTime,
      actual =
        GetNextTodayAlarmUseCase(
            alarmRepository(
              listOf(
                alarmModel(fireAtTime = LocalTime(12, 25), isOn = true),
                alarmModel(fireAtTime = LocalTime(12, 35), isOn = true),
                alarmModel(fireAtTime = LocalTime(15, 15), isOn = true),
                alarmModel(fireAtTime = expectedFireAtTime.time, isOn = true),
              )
            )
          )(now = LocalDateTime(expectedFireAtTime.date, LocalTime(11, 15, 45)))
          ?.fireOnDateTime,
    )
  }

  @Test
  fun `given multiple on alarms scheduled for different days - then return next on alarm scheduled for today`() =
    runTest {
      val expectedFireAtTime = LocalDateTime(LocalDate.now(), LocalTime(11, 45))
      assertEquals(
        expected = expectedFireAtTime,
        actual =
          GetNextTodayAlarmUseCase(
              alarmRepository(
                listOf(
                  alarmModel(fireAtTime = LocalTime(12, 35), isOn = true),
                  alarmModel(fireAtTime = expectedFireAtTime.time, isOn = true),
                  alarmModel(fireAtTime = LocalTime(10, 15), isOn = true),
                  alarmModel(
                    fireAtTime = LocalTime(11, 40),
                    isOn = true,
                    scheduledOnDaysOfWeek =
                      setOf(expectedFireAtTime.date.plus(1L, DateTimeUnit.DAY).dayOfWeek),
                  ),
                  alarmModel(
                    fireAtTime = LocalTime(11, 40),
                    isOn = true,
                    scheduledOnDates = setOf(expectedFireAtTime.date.plus(1L, DateTimeUnit.DAY)),
                  ),
                )
              )
            )(now = LocalDateTime(expectedFireAtTime.date, LocalTime(11, 15, 45)))
            ?.fireOnDateTime,
      )
    }

  @Test
  fun `given only alarms with null fireOnDateTime - then return null`() = runTest {
    val today = LocalDate.now()
    assertNull(
      GetNextTodayAlarmUseCase(
          alarmRepository(
            listOf(
              alarmModel(
                fireAtTime = LocalTime(11, 40),
                isOn = true,
                scheduledOnDaysOfWeek = setOf(today.dayOfWeek),
                offOnDates = setOf(today),
              ),
              alarmModel(
                fireAtTime = LocalTime(11, 40),
                isOn = true,
                scheduledOnDates = setOf(today),
                offOnDates = setOf(today),
              ),
            )
          )
        )(now = LocalDateTime(today, LocalTime(11, 15, 45)))
        ?.fireOnDateTime
    )
  }

  private fun alarmRepository(onAlarms: List<AlarmModel>): AlarmRepository = mock {
    everySuspend { getAllOnAlarmsList() } returns onAlarms
  }
}
