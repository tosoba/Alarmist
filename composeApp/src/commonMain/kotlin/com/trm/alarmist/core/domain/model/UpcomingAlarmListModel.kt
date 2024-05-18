package com.trm.alarmist.core.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class UpcomingAlarmListModel(
  val id: Long,
  val groupId: Long?,
  val fireAtTime: LocalTime,
  val name: String?,
  val status: UpcomingAlarmListStatus,
  val fireOnDateTime: LocalDateTime?,
  val scheduledOnDaysOfWeek: List<DayOfWeek>,
  val scheduledOnDate: LocalDate?,
  val scheduledOnMultipleDates: Boolean,
)
