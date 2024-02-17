package com.trm.alarmist.core.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

data class AlarmListModel(
  val id: Long,
  val fireAtTime: LocalTime,
  val name: String?,
  val isOn: Boolean,
  val nextFireOnDateTime: LocalDateTime?,
)
