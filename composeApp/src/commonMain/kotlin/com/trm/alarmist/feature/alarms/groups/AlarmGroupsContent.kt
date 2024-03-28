package com.trm.alarmist.feature.alarms.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.AlarmListItem
import com.trm.alarmist.core.ui.ExpandableAlarmGroupHeaderCard
import com.trm.alarmist.core.ui.ExpandableIcon

@Composable
fun AlarmGroupsContent(
  modifier: Modifier = Modifier,
  state: AlarmGroupsState = AlarmGroupsState(),
  onExpandGroup: (AlarmGroupModel) -> Unit = {},
  onCollapseGroup: () -> Unit = {},
  onAlarmItemClick: (AlarmListModel) -> Unit = {},
  onEditGroupClick: (AlarmGroupModel) -> Unit = {},
  onToggleAlarmOnOff: (AlarmListModel) -> Unit = {},
  onToggleGroupOnOff: (AlarmGroupModel) -> Unit = {},
) {
  LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
  ) {
    if (state.groups.isEmpty()) {
      item {
        Box(modifier = Modifier.fillParentMaxSize()) {
          Text(modifier = Modifier.align(Alignment.Center), text = "No groups created")
        }
      }
    }

    state.groups.forEachIndexed { groupIndex, group ->
      val isExpanded = state.expandedGroupId == group.id

      item(key = "group-${group.id}") {
        ExpandableAlarmGroupHeaderCard(
          group = group,
          modifier =
            Modifier.fillMaxWidth().padding(top = if (groupIndex > 0) 16.dp else 0.dp).clickable {
              if (group.alarmsCount > 0L) {
                if (isExpanded) onCollapseGroup() else onExpandGroup(group)
              } else {
                onEditGroupClick(group)
              }
            },
          isExpanded = isExpanded,
          shape =
            if (isExpanded) {
              ShapeDefaults.Medium.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp),
              )
            } else {
              ShapeDefaults.Medium
            },
          trailing = {
            if (group.alarmsCount > 0L) {
              Column(
                modifier = Modifier.padding(end = 16.dp),
                horizontalAlignment = Alignment.End,
              ) {
                Switch(checked = group.isOn, onCheckedChange = { _ -> onToggleGroupOnOff(group) })
                Spacer(Modifier.weight(1f))
                ExpandableIcon(isExpanded = isExpanded, transitionLabel = "${group.name}Header")
              }
            }
          },
        )
      }

      if (isExpanded && state.expandedGroupAlarms.isNotEmpty()) {
        items(items = state.expandedGroupAlarms, key = { alarm -> "alarm-${alarm.id}" }) { alarm ->
          AlarmListItem(
            item = alarm,
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            onItemClick = onAlarmItemClick,
            onToggleOnOff = remember { { onToggleAlarmOnOff(alarm) } },
          )
        }
      }

      if (isExpanded) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
              if (group.isOn) {
                CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
              } else {
                CardDefaults.cardColors()
              },
            shape =
              ShapeDefaults.Medium.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp)),
          ) {
            Box(Modifier.fillMaxWidth()) {
              TextButton(
                onClick = remember { { onEditGroupClick(group) } },
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
              ) {
                Text("Edit group")
              }
            }
          }
        }
      }
    }
  }
}
