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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.elevatedIf
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlarmGroupsList(
  groups: List<AlarmGroupModel>,
  modifier: Modifier = Modifier,
  expandedGroupId: Long? = null,
  expandedGroupAlarms: List<AlarmListModel> = emptyList(),
  onExpandGroup: (AlarmGroupModel) -> Unit = {},
  onCollapseGroup: () -> Unit = {},
  onEditGroupClick: (AlarmGroupModel) -> Unit = {},
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
              val firstInLastRowAlarmIndex =
                expandedGroupAlarms.indices.lastOrNull { it % fullSpan == 0 }
              val lastInLastRowAlarmIndex =
                expandedGroupAlarms.indices.lastOrNull { it % fullSpan == fullSpan - 1 }

              itemsIndexed(
                items = expandedGroupAlarms,
                key = { _, alarm -> "alarm-${alarm.id}" },
              ) { index, alarm ->
                Box(modifier = Modifier.fillMaxWidth()) {
                  AlarmListItem(
                    item = alarm,
                    modifier = Modifier.fillMaxWidth(),
                    shape =
                      groupedAlarmItemShape(
                        index = index,
                        firstInLastRowAlarmIndex = firstInLastRowAlarmIndex,
                        lastInLastRowAlarmIndex = lastInLastRowAlarmIndex,
                        fullSpan = fullSpan,
                        groupAlarmsCount = expandedGroupAlarms.size,
                      ),
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
                    if (group.alarmsCount.toInt() % fullSpan == 0) {
                      ShapeDefaults.Medium.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp),
                      )
                    } else {
                      ShapeDefaults.Medium.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp),
                      )
                    },
                  elevation = CardDefaults.elevatedIf(group.isOn),
                ) {
                  Box(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                      modifier =
                        Modifier.fillMaxWidth()
                          .padding(horizontal = 16.dp)
                          .align(Alignment.TopCenter)
                    )

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
}
