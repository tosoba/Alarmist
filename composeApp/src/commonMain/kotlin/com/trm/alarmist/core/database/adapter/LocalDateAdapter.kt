package com.trm.alarmist.core.database.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.LocalDate

object LocalDateAdapter : ColumnAdapter<LocalDate, String> {
  override fun decode(databaseValue: String) = LocalDate.parse(databaseValue)

  override fun encode(value: LocalDate) = value.toString()
}
