package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toLocalDate
import com.trm.alarmist.core.common.util.toLocalTime
import com.trm.alarmist.core.data.AlarmLocalRepository
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.util.addTestAlarm
import com.trm.alarmist.core.util.createTestAlarmLocalRepository
import dev.mokkery.MockMode
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlin.test.BeforeTest
import kotlin.test.Test

class TurnAlarmOffOnDateUseCaseTests {
  private val dispatcher = StandardTestDispatcher()

  private lateinit var repo: AlarmLocalRepository

  @BeforeTest
  fun before() {
    repo = createTestAlarmLocalRepository(dispatcher)
  }

  @Test
  fun `given on alarm scheduled for 2 future dates - when turn off on later date is called - then no scheduler calls`() =
    runTest(dispatcher) {
      val nextScheduledAt = now().plus(1L, DateTimeUnit.HOUR)
      val nextScheduledAtDate = nextScheduledAt.toLocalDate()
      val turnOffAtDate = nextScheduledAtDate.plus(1L, DateTimeUnit.DAY)
      val scheduler = mock<AlarmScheduler>(MockMode.autoUnit)

      TurnAlarmOffOnDateUseCase(repo, scheduler, mock(MockMode.autoUnit))(
        id =
          repo.addTestAlarm(
            fireAtTime = nextScheduledAt.toLocalTime(),
            isOn = true,
            scheduledOnDates = listOf(nextScheduledAtDate, turnOffAtDate),
          ),
        date = turnOffAtDate,
      )

      verifyNoMoreCalls(scheduler)
    }

  @Test
  fun `given on alarm scheduled for 2 future dates - when turn off on earlier date is called - then scheduleAlarm is called`() =
    runTest(dispatcher) {
      val nextScheduledAt = now().plus(1L, DateTimeUnit.HOUR)
      val turnOffAtDate = nextScheduledAt.toLocalDate()
      val scheduler = mock<AlarmScheduler>(MockMode.autoUnit)
      val id =
        repo.addTestAlarm(
          fireAtTime = nextScheduledAt.toLocalTime(),
          isOn = true,
          scheduledOnDates = listOf(turnOffAtDate, turnOffAtDate.plus(1L, DateTimeUnit.DAY)),
        )

      TurnAlarmOffOnDateUseCase(repo, scheduler, mock(MockMode.autoUnit))(
        id = id,
        date = turnOffAtDate,
      )

      verify(VerifyMode.exactly(1)) {
        scheduler.scheduleAlarm(id, any(), any(), any(), any(), any(), any(), any())
      }
      verifyNoMoreCalls(scheduler)
    }

  @Test
  fun `given on alarm scheduled for 1 future date - when turn off on that date is called - then cancelAlarm is called`() =
    runTest(dispatcher) {
      val nextScheduledAt = now().plus(1L, DateTimeUnit.HOUR)
      val turnOffAtDate = nextScheduledAt.toLocalDate()
      val scheduler = mock<AlarmScheduler>(MockMode.autoUnit)
      val id =
        repo.addTestAlarm(
          fireAtTime = nextScheduledAt.toLocalTime(),
          isOn = true,
          scheduledOnDates = listOf(turnOffAtDate),
        )

      TurnAlarmOffOnDateUseCase(repo, scheduler, mock(MockMode.autoUnit))(
        id = id,
        date = turnOffAtDate,
      )

      verify(VerifyMode.exactly(1)) { scheduler.cancelAlarm(id) }
      verifyNoMoreCalls(scheduler)
    }
}
