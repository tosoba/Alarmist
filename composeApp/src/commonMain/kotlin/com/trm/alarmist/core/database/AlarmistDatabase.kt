package com.trm.alarmist.core.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.trm.alarmist.core.database.adapter.LocalTimeAdapter
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.AlarmistDb
import com.trm.alarmist.db.AlarmistQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

class AlarmistDatabase(
  databaseDriverFactory: SqlDriverFactory,
  private val dispatcher: CoroutineDispatcher,
) {
  private val database: AlarmistDb =
    AlarmistDb(
      driver = databaseDriverFactory.createDriver(),
      alarmAdapter = Alarm.Adapter(LocalTimeAdapter),
    )

  private val queries: AlarmistQueries = database.alarmistQueries

  suspend fun insertAlarm(
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ): Long =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.insertAlarm(
          id = null,
          groupId = null,
          fireAtTime = fireAtTime,
          name = name,
          isOn = if (isOn) 1L else 0L,
          scheduledOnDaysOfWeek = daysOfWeekToDbString(scheduledOnDaysOfWeek),
          scheduledOnDates = datesToDbString(scheduledOnDates),
          offOnDates = datesToDbString(offOnDates),
        )
        queries.selectLastInsertedRowId().executeAsOne()
      }
    }

  suspend fun updateAlarm(
    id: Long,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    withContext(dispatcher) {
      queries.updateAlarm(
        id = id,
        groupId = null,
        fireAtTime = fireAtTime,
        name = name,
        isOn = if (isOn) 1L else 0L,
        scheduledOnDaysOfWeek = daysOfWeekToDbString(scheduledOnDaysOfWeek),
        scheduledOnDates = datesToDbString(scheduledOnDates),
        offOnDates = datesToDbString(offOnDates),
      )
    }
  }

  fun selectAllAlarms(): Flow<List<Alarm>> =
    queries.selectAllAlarms().asFlow().mapToList(dispatcher)

  suspend fun updateToggleAlarmOnOff(id: Long): Alarm =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateToggleAlarmOnOffById(id)
        queries.selectAlarmById(id).executeAsOne()
      }
    }

  suspend fun selectAlarmById(id: Long): Alarm =
    withContext(dispatcher) { queries.selectAlarmById(id).executeAsOne() }

  private fun daysOfWeekToDbString(daysOfWeek: Collection<DayOfWeek>): String? =
    daysOfWeek
      .takeIf(Collection<DayOfWeek>::isNotEmpty)
      ?.joinToString(separator = ",", transform = { it.isoDayNumber.toString() })

  private fun datesToDbString(dates: Collection<LocalDate>): String? =
    dates
      .takeIf(Collection<LocalDate>::isNotEmpty)
      ?.joinToString(separator = ",", transform = LocalDate::toString)
}
