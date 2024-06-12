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
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

class TurnAlarmOnOnDateUseCaseTests {
  private val dispatcher = StandardTestDispatcher()

  private lateinit var repo: AlarmLocalRepository

  @BeforeTest
  fun before() {
    repo = createTestAlarmLocalRepository(dispatcher)
  }

  @Test
  fun `given on alarm scheduled for 2 future dates - when turn on on later date is called - then no scheduler calls`() =
    runTest(dispatcher) {
      val nextScheduledAt = now().plus(1L, DateTimeUnit.HOUR)
      val nextScheduledAtDate = nextScheduledAt.toLocalDate()
      val turnOnAtDate = nextScheduledAtDate.plus(1L, DateTimeUnit.DAY)
      val scheduler = mock<AlarmScheduler>(MockMode.autoUnit)

      TurnAlarmOnOnDateUseCase(repo, scheduler)(
        id =
          repo.addTestAlarm(
            fireAtTime = nextScheduledAt.toLocalTime(),
            isOn = true,
            scheduledOnDates = listOf(nextScheduledAtDate, turnOnAtDate),
          ),
        date = turnOnAtDate,
      )

      verifyNoMoreCalls(scheduler)
    }

  @Test
  fun `given on alarm scheduled for 2 future dates - when turn on on earlier date is called - then scheduleAlarm is called`() =
    runTest(dispatcher) {
      val nextScheduledAt = now().plus(1L, DateTimeUnit.HOUR)
      val turnOnAtDate = nextScheduledAt.toLocalDate()
      val scheduler = mock<AlarmScheduler>(MockMode.autoUnit)
      val id =
        repo.addTestAlarm(
          fireAtTime = nextScheduledAt.toLocalTime(),
          isOn = true,
          scheduledOnDates = listOf(turnOnAtDate, turnOnAtDate.plus(1L, DateTimeUnit.DAY)),
        )

      TurnAlarmOnOnDateUseCase(repo, scheduler)(id = id, date = turnOnAtDate)

      verify(VerifyMode.exactly(1)) {
        scheduler.scheduleAlarm(eq(id), any(), any(), any(), any(), any(), any(), any(), any())
      }
    }

  @Test
  fun `given on alarm scheduled for 1 future date - when turn on on that date is called - then scheduleAlarm is called`() =
    runTest(dispatcher) {
      val nextScheduledAt = now().plus(1L, DateTimeUnit.HOUR)
      val turnOnAtDate = nextScheduledAt.toLocalDate()
      val scheduler = mock<AlarmScheduler>(MockMode.autoUnit)
      val id =
        repo.addTestAlarm(
          fireAtTime = nextScheduledAt.toLocalTime(),
          isOn = true,
          scheduledOnDates = listOf(turnOnAtDate),
        )

      TurnAlarmOnOnDateUseCase(repo, scheduler)(id = id, date = turnOnAtDate)

      verify(VerifyMode.exactly(1)) {
        scheduler.scheduleAlarm(any(), any(), any(), any(), any(), any(), any(), any(), any())
      }
    }
}
