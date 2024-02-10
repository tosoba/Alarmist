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
import kotlinx.datetime.LocalTime

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

  suspend fun insertAlarm(name: String?, fireAt: LocalTime): Long =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.insertAlarm(
          id = null,
          groupId = null,
          name = name,
          fireAt = fireAt,
          off = 0L,
          scheduledOnDaysOfWeek = null,
          scheduledOnDates = null,
          pausedOnDates = null,
        )
        queries.selectLastInsertedRowId().executeAsOne()
      }
    }

  fun selectAllAlarms(): Flow<List<Alarm>> =
    queries.selectAllAlarms().asFlow().mapToList(dispatcher)
}
