package com.trm.alarmist.core.database

import app.cash.sqldelight.db.SqlDriver

expect class SqlDriverFactory {
  fun createDriver(): SqlDriver
}

const val DB_NAME = "Alarmist.db"
