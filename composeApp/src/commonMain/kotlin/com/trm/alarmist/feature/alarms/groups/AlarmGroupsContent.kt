package com.trm.alarmist.feature.alarms.groups

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.create_alarm_using_button
import alarmist.composeapp.generated.resources.edit_group
import alarmist.composeapp.generated.resources.no_alarm_groups_created
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.elevatedIf
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.AlarmGroupHeaderCard
import com.trm.alarmist.core.ui.AlarmListItem
import com.trm.alarmist.core.ui.EmptyPlaceholder
import com.trm.alarmist.core.ui.ExpandableIcon
import com.trm.alarmist.core.ui.FloatingActionButtonSpacer
import com.trm.alarmist.core.ui.floatingActionButtonSpacerItem
import com.trm.alarmist.feature.alarm.AlarmPermissionStatusCard
import com.trm.alarmist.core.system.permission.isPostNotificationPermissionGranted
import org.jetbrains.compose.resources.stringResource

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

  Crossfade(state.groups.isEmpty(), modifier = modifier) { groupsEmpty ->
    if (groupsEmpty) {
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
          EmptyPlaceholder(
            imageVector = Icons.Default.CreateNewFolder,
            primaryText = stringResource(Res.string.no_alarm_groups_created),
            secondaryText = stringResource(Res.string.create_alarm_using_button),
            modifier = Modifier.align(Alignment.Center),
          )
        }

        FloatingActionButtonSpacer()
      }
    } else {
      LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      ) {
        if (!alarmPermissionGranted) {
          item {
            AlarmPermissionStatusCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
          }
        }

        state.groups.forEachIndexed { groupIndex, group ->
          val isExpanded = state.expandedGroupId == group.id

          item(key = "group-${group.id}") {
            val shape =
              if (isExpanded) {
                ShapeDefaults.Medium.copy(
                  bottomStart = CornerSize(0.dp),
                  bottomEnd = CornerSize(0.dp),
                )
              } else {
                ShapeDefaults.Medium
              }
            AlarmGroupHeaderCard(
              group = group,
              modifier =
                Modifier.fillMaxWidth()
                  .padding(top = if (groupIndex > 0) 16.dp else 0.dp)
                  .clip(shape)
                  .clickable {
                    if (group.alarmsCount > 0L) {
                      if (isExpanded) onCollapseGroup() else onExpandGroup(group)
                    } else {
                      onEditGroupClick(group)
                    }
                  },
              shape = shape,
              trailing = {
                if (group.alarmsCount > 0L) {
                  Column(
                    modifier = Modifier.padding(end = 16.dp),
                    horizontalAlignment = Alignment.End,
                  ) {
                    Switch(
                      checked = group.isOn,
                      onCheckedChange = { _ -> onToggleGroupOnOff(group) },
                    )
                    Spacer(Modifier.weight(1f))
                    ExpandableIcon(isExpanded = isExpanded, transitionLabel = "${group.name}Header")
                  }
                }
              },
            )
          }

          if (isExpanded && state.expandedGroupAlarms.isNotEmpty()) {
            items(state.expandedGroupAlarms, key = { alarm -> "alarm-${alarm.id}" }) { alarm ->
              Box(modifier = Modifier.fillMaxWidth()) {
                AlarmListItem(
                  item = alarm,
                  modifier = Modifier.fillMaxWidth(),
                  shape = RectangleShape,
                  onItemClick = onAlarmItemClick,
                  onToggleOnOff = remember(alarm) { { onToggleAlarmOnOff(alarm) } },
                )

                HorizontalDivider(
                  modifier =
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.TopCenter)
                )
              }
            }
          }

          if (isExpanded && group.alarmsCount > 0L) {
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
                elevation = CardDefaults.elevatedIf(group.isOn),
              ) {
                Box(Modifier.fillMaxWidth()) {
                  TextButton(
                    onClick = remember(group) { { onEditGroupClick(group) } },
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                  ) {
                    Text(stringResource(Res.string.edit_group))
                  }
                }
              }
            }
          }
        }

        floatingActionButtonSpacerItem()
      }
    }
  }
}
