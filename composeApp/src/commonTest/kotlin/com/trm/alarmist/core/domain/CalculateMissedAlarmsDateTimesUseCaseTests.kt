package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toLocalDateDefault
import com.trm.alarmist.core.common.util.toLocalDateTimeDefault
import com.trm.alarmist.core.common.util.toLocalTimeDefault
import com.trm.alarmist.core.domain.usecase.CalculateMissedAlarmsDateTimesUseCase
import com.trm.alarmist.core.util.alarmModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalculateMissedAlarmsDateTimesUseCaseTests {
  @Test
  fun `given empty alarms - then return emptyMap`() = runTest {
    assertEquals(
      expected = emptyMap(),
      actual =
        CalculateMissedAlarmsDateTimesUseCase(
          repository =
            mock<AlarmRepository>().apply { everySuspend { getAllOnAlarms() } returns emptyList() }
        )(),
    )
  }

  @Test
  fun `given only alarms with null lastNotificationDate that was not missed - then return emptyMap`() =
    runTest {
      val now = Clock.System.now()
      assertEquals(
        expected = emptyMap(),
        actual =
          CalculateMissedAlarmsDateTimesUseCase(
            repository =
              mock<AlarmRepository>().apply {
                everySuspend { getAllOnAlarms() } returns
                  listOf(
                    alarmModel(
                      fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
                      lastModificationDateTime =
                        now.minus(1, DateTimeUnit.HOUR).toLocalDateTimeDefault(),
                    ),
                    alarmModel(
                      fireAtTime = now.minus(2, DateTimeUnit.HOUR).toLocalTimeDefault(),
                      lastModificationDateTime =
                        now.minus(1, DateTimeUnit.HOUR).toLocalDateTimeDefault(),
                    ),
                  )
              }
          )(),
      )
    }

  @Test
  fun `given only alarm with non null lastNotificationDate that was not missed - then return emptyMap`() =
    runTest {
      val now = Clock.System.now()
      assertEquals(
        expected = emptyMap(),
        actual =
          CalculateMissedAlarmsDateTimesUseCase(
            repository =
              mock<AlarmRepository>().apply {
                everySuspend { getAllOnAlarms() } returns
                  listOf(
                    alarmModel(
                      fireAtTime = now.minus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
                      lastModificationDateTime =
                        now.minus(2, DateTimeUnit.HOUR).toLocalDateTimeDefault(),
                      lastNotificationDate = now.minus(1, DateTimeUnit.HOUR).toLocalDateDefault(),
                    ),
                    alarmModel(
                      fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
                      lastModificationDateTime =
                        LocalDateTime(
                          date = now.toLocalDateDefault().minus(1, DateTimeUnit.DAY),
                          time = now.minus(2, DateTimeUnit.HOUR).toLocalTimeDefault(),
                        ),
                      lastNotificationDate =
                        now
                          .plus(1, DateTimeUnit.HOUR)
                          .toLocalDateDefault()
                          .minus(1, DateTimeUnit.DAY),
                    ),
                  )
              }
          )(),
      )
    }
}
