package com.trm.alarmist.feature.alarm

import androidx.compose.runtime.Immutable
import com.trm.alarmist.core.common.util.nextFullHour
import com.trm.alarmist.core.common.util.now
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AlarmState(
  val fireAtTime: LocalTime = LocalTime(now().nextFullHour(), 0),
  val name: String? = null,
  val scheduledOnDaysOfWeek: Set<DayOfWeek> = emptySet(),
  val scheduledOnDates: Set<LocalDate> = emptySet(),
  val offOnDates: Set<LocalDate> = emptySet(),
)
