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
  val date: LocalDate?,
  val name: String?,
  val status: UpcomingAlarmListStatus,
  val fireOnDateTime: LocalDateTime?,
  val scheduledOnDaysOfWeek: Set<DayOfWeek>,
  val scheduledOnMultipleDates: Boolean,
)
