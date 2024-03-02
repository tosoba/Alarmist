package com.trm.alarmist.feature.alarms.groups

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.ui.ExpandableAlarmGroupHeaderCard
import com.trm.alarmist.core.ui.GroupedAlarmCard

@Composable
fun AlarmGroupsContent(
  modifier: Modifier = Modifier,
  state: AlarmGroupsState = AlarmGroupsState(),
  onExpandGroup: (AlarmGroupModel) -> Unit = {},
  onCollapseGroup: () -> Unit = {},
) {
  LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
    state.groups.forEach { group ->
      val isExpanded = state.expandedGroupId == group.id

      item(key = group.id) {
        if (group.alarmsCount == 0L) {
          ElevatedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text(modifier = Modifier.padding(16.dp), text = "${group.name} - empty")
          }
        } else {
          ExpandableAlarmGroupHeaderCard(
            group = group,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            isExpanded = isExpanded,
            onToggleExpandedClick = { if (isExpanded) onCollapseGroup() else onExpandGroup(group) },
          )
        }
      }

      if (isExpanded && state.expandedGroupAlarms.isNotEmpty()) {
        itemsIndexed(state.expandedGroupAlarms) { index, alarm ->
          GroupedAlarmCard(
            alarm = alarm,
            modifier = Modifier.fillMaxWidth(),
            shape =
              if (index == state.expandedGroupAlarms.lastIndex) {
                ShapeDefaults.Medium.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))
              } else {
                RectangleShape
              },
            colors =
              if (alarm.isOn) {
                CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
              } else {
                CardDefaults.cardColors()
              },
            isSelected = false,
            onToggleAlarmSelection = {},
          )
        }
      }
    }
  }
}
