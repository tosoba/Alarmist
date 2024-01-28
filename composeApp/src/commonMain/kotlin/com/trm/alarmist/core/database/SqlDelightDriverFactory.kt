package com.trm.alarmist.core.database

import app.cash.sqldelight.db.SqlDriver

internal expect class DriverFactory {
  fun createDriver(): SqlDriver
}

internal const val DB_NAME = "Alarmist.db"
