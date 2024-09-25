package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetNextTodayAlarmUseCase
import com.trm.alarmist.core.util.alarmListModel
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
  fun `given only on alarms with null fireOnDateTimes - then return null`() = runTest {
    assertNull(
      GetNextTodayAlarmUseCase(
        alarmRepository(onAlarms = List(10) { alarmListModel(fireOnDateTime = null, isOn = false) })
      )(now = LocalDateTime.now())
    )
  }

  @Test
  fun `given multiple on alarms scheduled for today - then return next on alarm`() {
    val expectedFireAtTime = LocalDateTime(LocalDate.now(), LocalTime(13, 35))
    runTest {
      assertEquals(
        expected = expectedFireAtTime,
        actual =
          GetNextTodayAlarmUseCase(
              alarmRepository(
                listOf(
                  alarmListModel(
                    fireOnDateTime = LocalDateTime(LocalDate.now(), LocalTime(20, 5)),
                    isOn = true,
                  ),
                  alarmListModel(fireOnDateTime = expectedFireAtTime, isOn = true),
                  alarmListModel(
                    fireOnDateTime = LocalDateTime(LocalDate.now(), LocalTime(15, 10)),
                    isOn = true,
                  ),
                )
              )
            )(now = LocalDateTime(expectedFireAtTime.date, LocalTime(11, 15, 45)))
            ?.fireOnDateTime,
      )
    }
  }

  @Test
  fun `given multiple on alarms scheduled for various days - then return next on alarm scheduled for today`() {
    val expectedFireAtTime = LocalDateTime(LocalDate.now(), LocalTime(12, 35))
    runTest {
      assertEquals(
        expected = expectedFireAtTime,
        actual =
          GetNextTodayAlarmUseCase(
              alarmRepository(
                listOf(
                  alarmListModel(
                    fireOnDateTime =
                      LocalDateTime(LocalDate.now().plus(1L, DateTimeUnit.DAY), LocalTime(12, 25)),
                    isOn = true,
                  ),
                  alarmListModel(fireOnDateTime = expectedFireAtTime, isOn = true),
                  alarmListModel(
                    fireOnDateTime = LocalDateTime(LocalDate.now(), LocalTime(15, 15)),
                    isOn = true,
                  ),
                  alarmListModel(
                    fireOnDateTime =
                      LocalDateTime(LocalDate.now().plus(2L, DateTimeUnit.DAY), LocalTime(11, 45)),
                    isOn = true,
                  ),
                )
              )
            )(now = LocalDateTime(expectedFireAtTime.date, LocalTime(11, 15, 45)))
            ?.fireOnDateTime,
      )
    }
  }

  private fun alarmRepository(onAlarms: List<AlarmListModel>): AlarmRepository = mock {
    everySuspend { getAllOnAlarmsList() } returns onAlarms
  }
}
