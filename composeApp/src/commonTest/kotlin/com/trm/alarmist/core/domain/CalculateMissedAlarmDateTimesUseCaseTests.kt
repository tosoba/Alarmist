package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.toLocalDateDefault
import com.trm.alarmist.core.common.util.toLocalDateTimeDefault
import com.trm.alarmist.core.common.util.toLocalTimeDefault
import com.trm.alarmist.core.domain.usecase.calculateAlarmMissedDateTimes
import com.trm.alarmist.core.util.alarmModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalculateMissedAlarmDateTimesUseCaseTests {
  // TODO: split/modify tests so they make more sense
  @Test
  fun `given alarm with null lastNotificationDate that was not missed - then return emptyList`() =
    runTest {
      val now = Clock.System.now()
      assertEquals(
        expected = emptyList(),
        actual =
          calculateAlarmMissedDateTimes(
            alarmModel(
              fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
              lastModificationDateTime = now.minus(1, DateTimeUnit.HOUR).toLocalDateTimeDefault(),
            )
          ),
      )
      assertEquals(
        expected = emptyList(),
        actual =
          calculateAlarmMissedDateTimes(
            alarmModel(
              fireAtTime = now.minus(2, DateTimeUnit.HOUR).toLocalTimeDefault(),
              lastModificationDateTime = now.minus(1, DateTimeUnit.HOUR).toLocalDateTimeDefault(),
            )
          ),
      )
    }

  @Test
  fun `given only alarm with non null lastNotificationDate that was not missed - then return emptyList`() =
    runTest {
      val now = Clock.System.now()
      assertEquals(
        expected = emptyList(),
        actual =
          calculateAlarmMissedDateTimes(
            alarmModel(
              fireAtTime = now.minus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
              lastModificationDateTime = now.minus(2, DateTimeUnit.HOUR).toLocalDateTimeDefault(),
              lastNotificationDate = now.minus(1, DateTimeUnit.HOUR).toLocalDateDefault(),
            )
          ),
      )
      assertEquals(
        expected = emptyList(),
        actual =
          calculateAlarmMissedDateTimes(
            alarmModel(
              fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
              lastModificationDateTime =
                LocalDateTime(
                  date = now.toLocalDateDefault().minus(1, DateTimeUnit.DAY),
                  time = now.minus(2, DateTimeUnit.HOUR).toLocalTimeDefault(),
                ),
              lastNotificationDate =
                now.plus(1, DateTimeUnit.HOUR).toLocalDateDefault().minus(1, DateTimeUnit.DAY),
            )
          ),
      )
    }

  @Test
  fun `given one time alarms one of which was missed - then return missed alarm with single dateTime`() =
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
            lastNotificationDate = null,
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
          listOf(
            LocalDateTime(
              date = alarms.first().lastModificationDateTime.date,
              time = alarms.first().fireAtTime,
            )
          ),
        actual = calculateAlarmMissedDateTimes(alarms.first()),
      )
    }

  // TODO: test case for maxOf (when previously missed alarm was modified
}
