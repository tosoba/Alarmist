package com.trm.alarmist.core.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AlarmScheduleModel(
  val id: Long,
  val scheduledOnDaysOfWeek: Set<DayOfWeek>,
  val scheduledOnDates: Set<LocalDate>,
  val offOnDates: Set<LocalDate>,
)
