package com.trm.alarmist.feature.widget.group

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.serialization.Serializable

@Serializable
data class GroupWidgetConfigState(
  val groups: List<AlarmGroupModel> = emptyList(),
  val chosenGroupId: Long? = null,
  val expandedGroupId: Long? = null,
  val expandedGroupAlarms: List<AlarmListModel> = emptyList(),
)
