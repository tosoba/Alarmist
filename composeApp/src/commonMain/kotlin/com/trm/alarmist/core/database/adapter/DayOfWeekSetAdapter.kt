package com.trm.alarmist.core.database.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

object DayOfWeekSetAdapter : ColumnAdapter<Set<DayOfWeek>, String> {
  override fun decode(databaseValue: String): Set<DayOfWeek> =
    databaseValue
      .takeIf(String::isNotEmpty)
      ?.split(",")
      ?.map { DayOfWeek(isoDayNumber = it.toInt()) }
      .orEmpty()
      .toSet()

  override fun encode(value: Set<DayOfWeek>): String =
    value
      .takeIf(Collection<DayOfWeek>::isNotEmpty)
      ?.sorted()
      ?.joinToString(separator = ",", transform = { it.isoDayNumber.toString() })
      .orEmpty()
}
