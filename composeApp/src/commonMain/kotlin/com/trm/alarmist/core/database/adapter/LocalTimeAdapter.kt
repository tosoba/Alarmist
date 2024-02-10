package com.trm.alarmist.core.database.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.LocalTime

object LocalTimeAdapter : ColumnAdapter<LocalTime, String> {
  override fun decode(databaseValue: String) = LocalTime.parse(databaseValue)

  override fun encode(value: LocalTime) = value.toString()
}
