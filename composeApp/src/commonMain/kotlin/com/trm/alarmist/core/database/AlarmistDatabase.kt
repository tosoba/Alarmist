package com.trm.alarmist.core.database

import com.trm.alarmist.db.AlarmistDb
import kotlinx.coroutines.CoroutineDispatcher

internal class AlarmistDatabase(
  databaseDriverFactory: DriverFactory,
  private val dispatcher: CoroutineDispatcher
) {
  private val database: AlarmistDb = AlarmistDb(driver = databaseDriverFactory.createDriver())
}
