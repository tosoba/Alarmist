package com.trm.alarmist.feature.alarms.upcoming

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable

@Serializable
data class UpcomingAlarmsState(
  val selectedDate: LocalDate?,
  val currentMonth: Month,
  val currentYear: Int,
)
