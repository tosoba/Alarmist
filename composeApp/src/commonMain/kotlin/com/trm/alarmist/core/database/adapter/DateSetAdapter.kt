package com.trm.alarmist.core.database.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.LocalDate

object DateSetAdapter : ColumnAdapter<Set<LocalDate>, String> {
  override fun decode(databaseValue: String): Set<LocalDate> =
    databaseValue
      .takeIf(String::isNotEmpty)
      ?.split(",")
      ?.map(LocalDate.Companion::parse)
      .orEmpty()
      .toSet()

  override fun encode(value: Set<LocalDate>): String =
    value
      .takeIf(Collection<LocalDate>::isNotEmpty)
      ?.sorted()
      ?.joinToString(separator = ",", transform = LocalDate::toString)
      .orEmpty()
}
