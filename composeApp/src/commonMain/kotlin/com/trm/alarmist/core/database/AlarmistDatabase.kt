package com.trm.alarmist.core.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.trm.alarmist.core.database.adapter.LocalDateTimeAdapter
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.AlarmistDb
import com.trm.alarmist.db.AlarmistQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

class AlarmistDatabase(
  databaseDriverFactory: DriverFactory,
  private val dispatcher: CoroutineDispatcher
) {
  private val database: AlarmistDb =
    AlarmistDb(
      driver = databaseDriverFactory.createDriver(),
      alarmAdapter = Alarm.Adapter(LocalDateTimeAdapter)
    )

  private val queries: AlarmistQueries = database.alarmistQueries

  suspend fun insertAlarm(name: String, fireAt: LocalDateTime) {
    withContext(dispatcher) { queries.insertAlarm(id = null, name = name, fireAt = fireAt) }
  }

  fun selectAllAlarms(): Flow<List<Alarm>> =
    queries.selectAllAlarms().asFlow().mapToList(dispatcher)
}
