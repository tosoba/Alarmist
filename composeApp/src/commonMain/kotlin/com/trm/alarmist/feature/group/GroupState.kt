package com.trm.alarmist.feature.group

import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.serialization.Serializable

@Serializable
data class GroupState(
  val name: String? = "",
  val color: Long? = null,
  val ungroupedAlarms: List<AlarmListModel> = emptyList(),
  val selectedAlarmIds: Set<Long> = emptySet(),
)
