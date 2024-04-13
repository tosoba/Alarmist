package com.trm.alarmist.feature.group

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.serialization.Serializable

@Serializable
data class GroupState(
  val name: String = "",
  val color: Int = Color.Transparent.toArgb(),
  val blankNameError: Boolean = false,
  val alarms: Map<Long, List<AlarmListModel>> = emptyMap(),
  val groups: Map<Long, AlarmGroupModel> = emptyMap(),
  val selectedAlarmIds: Set<Long> = emptySet(),
) {
  constructor(group: AlarmGroupModel) : this(name = group.name, color = group.color.toInt())

  fun alarmsInGroup(id: Long): List<AlarmListModel> = alarms[id].orEmpty()
}
