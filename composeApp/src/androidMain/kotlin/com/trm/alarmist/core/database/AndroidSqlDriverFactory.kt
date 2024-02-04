package com.trm.alarmist.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.trm.alarmist.db.AlarmistDb

actual class SqlDriverFactory(private val context: Context) {
  actual fun createDriver(): SqlDriver = AndroidSqliteDriver(AlarmistDb.Schema, context, DB_NAME)
}
