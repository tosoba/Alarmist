package com.trm.alarmist.core.util

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.data.AlarmLocalRepository
import com.trm.alarmist.core.database.adapter.alarmAdapter
import com.trm.alarmist.db.AlarmistDb
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal suspend fun AlarmLocalRepository.addTestAlarm(
  groupId: Long? = null,
  fireAtTime: LocalTime = LocalTime.now(),
  name: String? = null,
  isOn: Boolean = true,
  scheduledOnDaysOfWeek: Collection<DayOfWeek> = emptyList(),
  scheduledOnDates: Collection<LocalDate> = emptyList(),
  offOnDates: Collection<LocalDate> = emptyList(),
  alarmDurationMinutes: Long = 2L,
  soundEnabled: Boolean = false,
  vibrationEnabled: Boolean = false,
  reminderOffsetHours: Long = 1L,
  soundId: String? = null,
): Long =
  addAlarm(
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn,
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
    scheduledOnDates = scheduledOnDates,
    offOnDates = offOnDates,
    alarmDurationMinutes = alarmDurationMinutes,
    soundEnabled = soundEnabled,
    vibrationEnabled = vibrationEnabled,
    reminderOffsetHours = reminderOffsetHours,
    soundId = soundId,
  )

internal fun createTestAlarmLocalRepository(dispatcher: CoroutineDispatcher): AlarmLocalRepository {
  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  val repo =
    AlarmLocalRepository(
      queries = AlarmistDb(driver = driver, alarmAdapter = alarmAdapter()).alarmistQueries,
      dispatcher = dispatcher,
    )
  AlarmistDb.Schema.create(driver)
  return repo
}
