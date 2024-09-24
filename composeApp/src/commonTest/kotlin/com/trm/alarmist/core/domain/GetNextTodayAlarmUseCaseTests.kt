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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class GetNextTodayAlarmUseCaseTests {
  @Test
  fun `given no alarms - then return null`() = runTest {
    assertNull(
      GetNextTodayAlarmUseCase(alarmRepository(onAlarms = emptyList()))(now = LocalDateTime.now())
    )
  }

  @Test
  fun `given multiple on alarms scheduled for today - then return next on alarm`() {
    val expectedFireAtTime = LocalDateTime(LocalDate.now(), LocalTime(13, 35))

    runTest {
      val result =
        GetNextTodayAlarmUseCase(
          alarmRepository(
            listOf(
              alarmListModel(
                fireOnDateTime = LocalDateTime(LocalDate.now(), LocalTime(10, 5)),
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

      assertNotNull(result)
      assertTrue(result.isOn)
      assertEquals(expectedFireAtTime, result.fireOnDateTime)
    }
  }

  private fun alarmRepository(onAlarms: List<AlarmListModel>): AlarmRepository = mock {
    everySuspend { getAllOnAlarmsList() } returns onAlarms
  }
}
