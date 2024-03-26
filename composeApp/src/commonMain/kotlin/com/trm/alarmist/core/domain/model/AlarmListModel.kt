package com.trm.alarmist.core.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class AlarmListModel(
  val id: Long,
  val groupId: Long?,
  val fireAtTime: LocalTime,
  val name: String?,
  val isOn: Boolean,
  val fireOnDateTime: LocalDateTime?,
  val scheduledOnDaysOfWeek: List<DayOfWeek>,
  val scheduledOnClosestDate: LocalDate?,
  val scheduledOnMultipleDates: Boolean
)
