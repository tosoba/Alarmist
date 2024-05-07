package com.trm.alarmist.core.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class AlarmScheduleModel(
  val id: Long,
  val fireAtTime: LocalTime,
  val scheduledOnDaysOfWeek: Set<DayOfWeek>,
  val scheduledOnDates: Set<LocalDate>,
  val offOnDates: Set<LocalDate>,
)
