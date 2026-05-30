package com.trm.alarmist.core.ui

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.create_alarm_using_button
import alarmist.composeapp.generated.resources.edit_group
import alarmist.composeapp.generated.resources.no_alarm_groups_created
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlarmGroupsList(
  groups: List<AlarmGroupModel>,
  modifier: Modifier = Modifier,
  expandedGroupId: Long? = null,
  expandedGroupAlarms: List<AlarmListModel> = emptyList(),
  onExpandGroup: (AlarmGroupModel) -> Unit,
  onCollapseGroup: () -> Unit,
  onEditGroupClick: (AlarmGroupModel) -> Unit,
  onAlarmItemClick: (AlarmListModel) -> Unit = {},
  onToggleAlarmOnOff: (AlarmListModel) -> Unit = {},
  headerItems: LazyGridScope.() -> Unit = {},
  groupHeaderCardTrailing: @Composable (AlarmGroupModel) -> Unit,
) {
  Crossfade(groups.isEmpty(), modifier = modifier) { groupsEmpty ->
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
      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val cellMinSize = 300.dp
        val contentPaddingHorizontal = 16.dp

        LazyVerticalGrid(
          modifier = Modifier.fillMaxSize(),
          columns = GridCells.Adaptive(minSize = cellMinSize),
          contentPadding = PaddingValues(horizontal = contentPaddingHorizontal, vertical = 8.dp),
        ) {
          headerItems()

          groups.forEachIndexed { groupIndex, group ->
            val isExpanded = expandedGroupId == group.id

            item(key = "group-${group.id}", span = { GridItemSpan(maxLineSpan) }) {
              AlarmGroupHeaderCard(
                group = group,
                onClick = {
                  if (group.alarmsCount > 0L) {
                    if (isExpanded) onCollapseGroup() else onExpandGroup(group)
                  } else {
                    onEditGroupClick(group)
                  }
                },
                modifier =
                  Modifier.fillMaxWidth().padding(top = if (groupIndex > 0) 16.dp else 0.dp),
                shape =
                  if (isExpanded) {
                    ShapeDefaults.Medium.copy(
                      bottomStart = CornerSize(0.dp),
                      bottomEnd = CornerSize(0.dp),
                    )
                  } else {
                    ShapeDefaults.Medium
                  },
                trailing = { groupHeaderCardTrailing(group) },
              )
            }

            val fullSpan =
              maxOf(
                ((maxWidth - contentPaddingHorizontal - contentPaddingHorizontal) / cellMinSize)
                  .toInt(),
                1,
              )
            if (isExpanded && expandedGroupAlarms.isNotEmpty()) {
              itemsIndexed(
                items = expandedGroupAlarms,
                key = { _, alarm -> "alarm-${alarm.id}" },
              ) { index, alarm ->
                AlarmListItem(
                  item = alarm,
                  modifier = Modifier.fillMaxWidth().offset(y = (-1).dp),
                  shape =
                    groupedAlarmItemShape(
                      index = index,
                      firstInLastRowAlarmIndex =
                        expandedGroupAlarms.indices.lastOrNull { it % fullSpan == 0 },
                      lastInLastRowAlarmIndex =
                        expandedGroupAlarms.indices.lastOrNull { it % fullSpan == fullSpan - 1 },
                      groupAlarmsLastIndex = expandedGroupAlarms.lastIndex,
                    ),
                  onItemClick = onAlarmItemClick,
                  onToggleOnOff = { onToggleAlarmOnOff(alarm) },
                )
              }
            }

            if (isExpanded && group.alarmsCount > 0L) {
              item(key = "group-${group.id}-edit", span = { GridItemSpan(maxLineSpan) }) {
                TextButton(
                  onClick = { onEditGroupClick(group) },
                  modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                ) {
                  Text(stringResource(Res.string.edit_group))
                }
              }
            }
          }

          floatingActionButtonSpacerItem()
        }
      }
    }
  }
}
