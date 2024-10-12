package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.toLocalDate
import com.trm.alarmist.core.common.util.toLocalDateTime
import com.trm.alarmist.core.common.util.toLocalTime
import com.trm.alarmist.core.util.alarmModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalculateMissedAlarmDateTimesUseCaseTests {
  @Test
  fun `given alarm with null lastNotificationDate that was not missed and will fire in 1 hour - then return emptyList`() {
    val now = Clock.System.now()
    assertEquals(
      expected = emptyList(),
      actual =
        calculateAlarmMissedDateTimes(
          alarmModel(
            fireAtTime = now.plus(1, DateTimeUnit.HOUR).toLocalTime(),
            lastModificationDateTime = now.minus(1, DateTimeUnit.HOUR).toLocalDateTime(),
            lastNotificationDate = null,
          )
        ),
    )
  }

  @Test
  fun `given alarm with null lastNotificationDate that was not missed and will fire in 23 hours - then return emptyList`() {
    val now = Clock.System.now()
    assertEquals(
      expected = emptyList(),
      actual =
        calculateAlarmMissedDateTimes(
          alarmModel(
            fireAtTime = now.minus(1, DateTimeUnit.HOUR).toLocalTime(),
            lastModificationDateTime = now.toLocalDateTime(),
            lastNotificationDate = null,
          )
        ),
    )
  }

  @Test
  fun `given not missed one time alarm with that fired 1 hour ago - then return emptyList`() {
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
  }

  @Test
  fun `given not missed one time alarm with that fired 23 hours ago - then return emptyList`() {
    val now = Clock.System.now()
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
  fun `given missed one time alarm that was missed once - then return single missed dateTime`() {
    val now = Clock.System.now()
    val alarm =
      alarmModel(
        fireAtTime = now.minus(1, DateTimeUnit.HOUR).toLocalTime(),
        lastModificationDateTime =
          LocalDateTime(
            date = now.toLocalDate().minus(1, DateTimeUnit.DAY),
            time = now.minus(2, DateTimeUnit.HOUR).toLocalTime(),
          ),
        lastNotificationDate = null,
      )

    assertEquals(
      expected =
        listOf(LocalDateTime(date = alarm.lastModificationDateTime.date, time = alarm.fireAtTime)),
      actual = calculateAlarmMissedDateTimes(alarm),
    )
  }

  // TODO: test case for maxOf (when previously missed alarm was modified
}
