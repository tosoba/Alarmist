package com.trm.alarmist.core.domain

import com.trm.alarmist.core.common.util.toLocalDate
import com.trm.alarmist.core.common.util.toLocalDateTime
import com.trm.alarmist.core.common.util.toLocalTime
import com.trm.alarmist.core.domain.usecase.calculateAlarmMissedDateTimes
import com.trm.alarmist.core.util.alarmModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalculateMissedAlarmDateTimesUseCaseTests {
  // TODO: split/modify tests so they make more sense
  @Test
  fun `given alarm with null lastNotificationDate that was not missed - then return emptyList`() {
    val now = Clock.System.now()
    assertEquals(
      expected = emptyList(),
      actual =
        calculateAlarmMissedDateTimes(
          alarmModel(
            fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTime(),
            lastModificationDateTime = now.minus(1, DateTimeUnit.HOUR).toLocalDateTime(),
          )
        ),
    )
    assertEquals(
      expected = emptyList(),
      actual =
        calculateAlarmMissedDateTimes(
          alarmModel(
            fireAtTime = now.minus(2, DateTimeUnit.HOUR).toLocalTime(),
            lastModificationDateTime = now.minus(1, DateTimeUnit.HOUR).toLocalDateTime(),
          )
        ),
    )
  }

  @Test
  fun `given only alarm with non null lastNotificationDate that was not missed - then return emptyList`() {
    val now = Clock.System.now()
    assertEquals(
      expected = emptyList(),
      actual =
        calculateAlarmMissedDateTimes(
          alarmModel(
            fireAtTime = now.minus(1, DateTimeUnit.HOUR).toLocalTime(),
            lastModificationDateTime = now.minus(2, DateTimeUnit.HOUR).toLocalDateTime(),
            lastNotificationDate = now.minus(1, DateTimeUnit.HOUR).toLocalDate(),
          )
        ),
    )
    assertEquals(
      expected = emptyList(),
      actual =
        calculateAlarmMissedDateTimes(
          alarmModel(
            fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTime(),
            lastModificationDateTime =
              LocalDateTime(
                date = now.toLocalDate().minus(1, DateTimeUnit.DAY),
                time = now.minus(2, DateTimeUnit.HOUR).toLocalTime(),
              ),
            lastNotificationDate =
              now.plus(1, DateTimeUnit.HOUR).toLocalDate().minus(1, DateTimeUnit.DAY),
          )
        ),
    )
  }

  @Test
  fun `given one time alarms one of which was missed - then return missed alarm with single dateTime`() {
    val now = Clock.System.now()
    val alarms =
      listOf(
        alarmModel(
          id = 1L,
          fireAtTime = now.minus(1, DateTimeUnit.HOUR).toLocalTime(),
          lastModificationDateTime =
            LocalDateTime(
              date = now.toLocalDate().minus(2, DateTimeUnit.DAY),
              time = now.minus(2, DateTimeUnit.HOUR).toLocalTime(),
            ),
          lastNotificationDate = null,
        ),
        alarmModel(
          id = 2L,
          fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTime(),
          lastModificationDateTime =
            LocalDateTime(
              date = now.toLocalDate().minus(2, DateTimeUnit.DAY),
              time = now.minus(2, DateTimeUnit.HOUR).toLocalTime(),
            ),
          lastNotificationDate = now.toLocalDate().minus(2, DateTimeUnit.DAY),
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
