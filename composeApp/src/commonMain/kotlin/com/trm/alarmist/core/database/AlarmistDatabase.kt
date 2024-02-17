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
    fireAt: LocalTime,
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
          fireAt = fireAt,
          name = name,
          isOn = if (isOn) 1L else 0L,
          scheduledOnDaysOfWeek =
            scheduledOnDaysOfWeek
              .takeIf(Collection<DayOfWeek>::isNotEmpty)
              ?.joinToString(separator = ",", transform = { it.isoDayNumber.toString() }),
          scheduledOnDates =
            scheduledOnDates
              .takeIf(Collection<LocalDate>::isNotEmpty)
              ?.joinToString(separator = ",", transform = LocalDate::toString),
          offOnDates =
            offOnDates
              .takeIf(Collection<LocalDate>::isNotEmpty)
              ?.joinToString(separator = ",", transform = LocalDate::toString),
        )
        queries.selectLastInsertedRowId().executeAsOne()
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
}
