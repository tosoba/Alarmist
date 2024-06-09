package com.trm.alarmist.core.domain.usecase

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toLocalDate
import com.trm.alarmist.core.common.util.toLocalTime
import com.trm.alarmist.core.data.AlarmLocalRepository
import com.trm.alarmist.core.database.adapter.alarmAdapter
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.db.AlarmistDb
import dev.mokkery.MockMode
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class TurnAlarmOffOnDateUseCaseTests {
  private lateinit var repo: AlarmLocalRepository
  private val dispatcher = StandardTestDispatcher()

  @BeforeTest
  fun before() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    repo =
      AlarmLocalRepository(
        queries = AlarmistDb(driver = driver, alarmAdapter = alarmAdapter()).alarmistQueries,
        dispatcher = dispatcher,
      )
    AlarmistDb.Schema.create(driver)
  }

  @Test
  fun `given on alarm scheduled for 2 future dates - when turn off on later date is called - then no scheduler calls`() =
    runTest(dispatcher) {
      val nextScheduledAt = now().plus(1L, DateTimeUnit.HOUR)
      val nextScheduledAtDate = nextScheduledAt.toLocalDate()
      val turnOffAtDate = nextScheduledAtDate.plus(1L, DateTimeUnit.DAY)
      val scheduler = mock<AlarmScheduler>(MockMode.autoUnit)

      TurnAlarmOffOnDateUseCase(repo, scheduler)(
        id =
          addAlarm(
            fireAtTime = nextScheduledAt.toLocalTime(),
            isOn = true,
            scheduledOnDaysOfWeek = emptyList(),
            scheduledOnDates = listOf(nextScheduledAtDate, turnOffAtDate),
            offOnDates = emptyList(),
          ),
        date = turnOffAtDate,
      )

      verify(VerifyMode.not) {
        scheduler.scheduleAlarm(any(), any(), any(), any(), any(), any(), any(), any(), any())
      }
      verify(VerifyMode.not) { scheduler.cancelAlarm(any()) }
    }

  private suspend fun addAlarm(
    groupId: Long? = null,
    fireAtTime: LocalTime = LocalTime.now(),
    name: String? = null,
    isOn: Boolean = true,
    scheduledOnDaysOfWeek: Collection<DayOfWeek> = emptyList(),
    scheduledOnDates: Collection<LocalDate> = emptyList(),
    offOnDates: Collection<LocalDate> = emptyList(),
    snoozeDurationMinutes: Long = 2L,
    snoozeLimit: Long = 2L,
    alarmDurationMinutes: Long = 2L,
    soundEnabled: Boolean = false,
    vibrationEnabled: Boolean = false,
    reminderOffsetHours: Long = 1L,
    soundId: String? = null,
  ): Long =
    repo.addAlarm(
      groupId = groupId,
      fireAtTime = fireAtTime,
      name = name,
      isOn = isOn,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
      snoozeDurationMinutes = snoozeDurationMinutes,
      snoozeLimit = snoozeLimit,
      alarmDurationMinutes = alarmDurationMinutes,
      soundEnabled = soundEnabled,
      vibrationEnabled = vibrationEnabled,
      reminderOffsetHours = reminderOffsetHours,
      soundId = soundId,
    )
}
