package com.trm.alarmist.core.database.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

object DayOfWeekListAdapter : ColumnAdapter<List<DayOfWeek>, String> {
  override fun decode(databaseValue: String): List<DayOfWeek> =
    databaseValue
      .takeIf(String::isNotEmpty)
      ?.split(",")
      ?.map { DayOfWeek(isoDayNumber = it.toInt()) }
      .orEmpty()

  override fun encode(value: List<DayOfWeek>): String =
    value
      .takeIf(Collection<DayOfWeek>::isNotEmpty)
      ?.sorted()
      ?.joinToString(separator = ",", transform = { it.isoDayNumber.toString() })
      .orEmpty()
}
