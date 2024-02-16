package com.trm.alarmist.feature.alarm

import com.trm.alarmist.core.common.util.nextFullHour
import com.trm.alarmist.core.common.util.now
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class AlarmState(
  val fireAt: LocalTime = LocalTime(now().nextFullHour(), 0),
  val name: String? = null,
  val scheduledOnDaysOfWeek: Set<DayOfWeek> = emptySet(),
  val scheduledOnDates: Set<LocalDate> = emptySet(),
  val pausedOnDates: Set<LocalDate> = emptySet(),
)
