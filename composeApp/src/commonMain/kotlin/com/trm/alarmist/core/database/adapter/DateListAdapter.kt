package com.trm.alarmist.core.database.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.LocalDate

object DateListAdapter : ColumnAdapter<List<LocalDate>, String> {
  override fun decode(databaseValue: String): List<LocalDate> =
    databaseValue.takeIf(String::isNotEmpty)?.split(",")?.map(LocalDate.Companion::parse).orEmpty()

  override fun encode(value: List<LocalDate>): String =
    value
      .takeIf(Collection<LocalDate>::isNotEmpty)
      ?.sorted()
      ?.joinToString(separator = ",", transform = LocalDate::toString)
      .orEmpty()
}
