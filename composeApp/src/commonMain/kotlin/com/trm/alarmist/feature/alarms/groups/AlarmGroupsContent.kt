package com.trm.alarmist.feature.alarms.groups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.system.permission.isPostNotificationPermissionGranted
import com.trm.alarmist.core.ui.AlarmGroupsList
import com.trm.alarmist.core.ui.ExpandableIcon
import com.trm.alarmist.feature.alarm.AlarmPermissionStatusCard

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
  val alarmPermissionGranted = isPostNotificationPermissionGranted()

  AlarmGroupsList(
    groups = state.groups,
    modifier = modifier,
    expandedGroupId = state.expandedGroupId,
    expandedGroupAlarms = state.expandedGroupAlarms,
    onExpandGroup = onExpandGroup,
    onCollapseGroup = onCollapseGroup,
    onEditGroupClick = onEditGroupClick,
    onAlarmItemClick = onAlarmItemClick,
    onToggleAlarmOnOff = onToggleAlarmOnOff,
    headerItems = {
      if (!alarmPermissionGranted && state.groups.any { it.alarmsCount > 0L }) {
        item {
          AlarmPermissionStatusCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        }
      }
    },
  ) { group ->
    if (group.alarmsCount > 0L) {
      Column(
        modifier = Modifier.padding(end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Switch(checked = group.isOn, onCheckedChange = { _ -> onToggleGroupOnOff(group) })
        Spacer(Modifier.weight(1f))
        ExpandableIcon(
          isExpanded = state.expandedGroupId == group.id,
          transitionLabel = "${group.name}Header",
        )
      }
    }
  }
}
