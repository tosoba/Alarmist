package com.trm.alarmist.core.domain

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
import kotlinx.datetime.atTime
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
            mock<AlarmRepository>().apply { everySuspend { getAndUpdateOnAlarms() } returns emptyList() }
        )(),
    )
  }

  @Test
  fun `given only alarms with null lastNotificationDate that were not missed - then return emptyMap`() =
    runTest {
      val now = Clock.System.now()
      assertEquals(
        expected = emptyMap(),
        actual =
          CalculateMissedAlarmsDateTimesUseCase(
            repository =
              mock<AlarmRepository>().apply {
                everySuspend { getAndUpdateOnAlarms() } returns
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
  fun `given only alarms with non null lastNotificationDate that were not missed - then return emptyMap`() =
    runTest {
      val now = Clock.System.now()
      assertEquals(
        expected = emptyMap(),
        actual =
          CalculateMissedAlarmsDateTimesUseCase(
            repository =
              mock<AlarmRepository>().apply {
                everySuspend { getAndUpdateOnAlarms() } returns
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

  @Test
  fun `given everyday alarms that were missed once - then return missed alarms with single timestamps`() =
    runTest {
      val now = Clock.System.now()
      val alarms =
        listOf(
          alarmModel(
            id = 1L,
            fireAtTime = now.minus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
            lastModificationDateTime =
              LocalDateTime(
                date = now.toLocalDateDefault().minus(2, DateTimeUnit.DAY),
                time = now.minus(2, DateTimeUnit.HOUR).toLocalTimeDefault(),
              ),
            lastNotificationDate = now.toLocalDateDefault().minus(1, DateTimeUnit.DAY),
          ),
          alarmModel(
            id = 2L,
            fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
            lastModificationDateTime =
              LocalDateTime(
                date = now.toLocalDateDefault().minus(2, DateTimeUnit.DAY),
                time = now.minus(2, DateTimeUnit.HOUR).toLocalTimeDefault(),
              ),
            lastNotificationDate = now.toLocalDateDefault().minus(2, DateTimeUnit.DAY),
          ),
        )

      assertEquals(
        expected =
          mapOf(
            alarms.first() to listOf(now.toLocalDateDefault().atTime(alarms.first().fireAtTime)),
            alarms.last() to
              listOf(
                now.toLocalDateDefault().minus(1, DateTimeUnit.DAY).atTime(alarms.last().fireAtTime)
              ),
          ),
        actual =
          CalculateMissedAlarmsDateTimesUseCase(
            repository =
              mock<AlarmRepository>().apply { everySuspend { getAndUpdateOnAlarms() } returns alarms }
          )(),
      )
    }

  // TODO: test case for maxOf (when previously missed alarm was modified
}
