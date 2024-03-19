package com.trm.alarmist.core.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AlarmScheduleModel(
  val id: Long,
  val scheduledOnDaysOfWeek: List<DayOfWeek>,
  val scheduledOnDates: List<LocalDate>,
  val offOnDates: List<LocalDate>,
)
