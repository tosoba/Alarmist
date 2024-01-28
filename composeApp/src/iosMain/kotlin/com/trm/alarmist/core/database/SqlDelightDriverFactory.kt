package com.trm.alarmist.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.trm.alarmist.db.AlarmistDb

internal actual class DriverFactory {
  actual fun createDriver(): SqlDriver = NativeSqliteDriver(AlarmistDb.Schema, DB_NAME)
}
