package com.trm.alarmist.core.database.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.LocalDateTime

object LocalDateTimeAdapter : ColumnAdapter<LocalDateTime, String> {
  override fun decode(databaseValue: String) = LocalDateTime.parse(databaseValue)

  override fun encode(value: LocalDateTime) = value.toString()
}
