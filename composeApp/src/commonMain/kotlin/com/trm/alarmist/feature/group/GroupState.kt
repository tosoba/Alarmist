package com.trm.alarmist.feature.group

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.serialization.Serializable

@Serializable
data class GroupState(
  val name: String? = "",
  val color: Int = Color.Transparent.toArgb(),
  val ungroupedAlarms: List<AlarmListModel> = emptyList(),
  val selectedAlarmIds: Set<Long> = emptySet(),
)
