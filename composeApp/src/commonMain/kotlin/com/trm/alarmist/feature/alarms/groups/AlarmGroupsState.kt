package com.trm.alarmist.feature.alarms.groups

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.serialization.Serializable

@Serializable
data class AlarmGroupsState(
  val groups: List<AlarmGroupModel> = emptyList(),
  val expandedGroupId: Long? = null,
  val expandedGroupAlarms: List<AlarmListModel> = emptyList(),
)
