package com.trm.alarmist.feature.alarms.groups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.ExpandableAlarmGroupHeaderCard
import com.trm.alarmist.core.ui.ExpandableIcon

@Composable
fun AlarmGroupsContent(
  modifier: Modifier = Modifier,
  state: AlarmGroupsState = AlarmGroupsState(),
  onExpandGroup: (AlarmGroupModel) -> Unit = {},
  onCollapseGroup: () -> Unit = {},
  onToggleAlarmOnOff: (AlarmListModel) -> Unit = {},
  onToggleGroupOnOff: (AlarmGroupModel) -> Unit = {},
) {
  LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
    state.groups.forEachIndexed { groupIndex, group ->
      val isExpanded = state.expandedGroupId == group.id

      item(key = "group-${group.id}") {
        val headerModifier =
          Modifier.fillMaxWidth().padding(top = if (groupIndex > 0) 16.dp else 0.dp)

        if (group.alarmsCount == 0L) {
          ElevatedCard(modifier = headerModifier) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(Icons.Default.Folder, contentDescription = group.name)

              Spacer(Modifier.width(16.dp))

              Column {
                Text(text = group.name, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Empty")
              }
            }
          }
        } else {
          ExpandableAlarmGroupHeaderCard(
            group = group,
            modifier = headerModifier,
            isExpanded = isExpanded,
            onToggleExpandedClick = { if (isExpanded) onCollapseGroup() else onExpandGroup(group) },
            trailing = {
              Column(
                modifier = Modifier.padding(end = 16.dp),
                horizontalAlignment = Alignment.End,
              ) {
                Switch(
                  checked = group.isOn,
                  onCheckedChange = { _ -> onToggleGroupOnOff(group) },
                  thumbContent = {
                    Icon(
                      imageVector =
                        if (group.isOn) Icons.Default.Pause else Icons.Default.PlayArrow,
                      contentDescription = null,
                    )
                  },
                )
                Spacer(Modifier.weight(1f))
                ExpandableIcon(isExpanded = isExpanded, transitionLabel = "${group.name}Header")
              }
            },
          )
        }
      }

      if (isExpanded && state.expandedGroupAlarms.isNotEmpty()) {
        itemsIndexed(
          items = state.expandedGroupAlarms,
          key = { _, alarm -> "alarm-${alarm.id}" },
        ) { alarmIndex, alarm ->
          AlarmCard(
            alarm = alarm,
            modifier = Modifier.fillMaxWidth(),
            shape =
              if (alarmIndex == state.expandedGroupAlarms.lastIndex) {
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
            onToggleAlarmOnOff = remember { { onToggleAlarmOnOff(alarm) } },
          )
        }
      }
    }
  }
}

@Composable
private fun AlarmCard(
  alarm: AlarmListModel,
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  colors: CardColors = CardDefaults.cardColors(),
  onToggleAlarmOnOff: () -> Unit = {},
) {
  Card(modifier = modifier, shape = shape, colors = colors) {
    Spacer(modifier = Modifier.height(8.dp))

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
      Column {
        Text(
          text = alarm.fireAtTime.toString(),
          style =
            MaterialTheme.typography.headlineLarge.run {
              if (alarm.isOn) copy(fontWeight = FontWeight.Medium) else this
            },
        )

        Spacer(modifier = Modifier.height(4.dp))

        alarm.name?.let { Text(it) }
      }

      Spacer(modifier = Modifier.weight(1f))

      Switch(
        checked = alarm.isOn,
        onCheckedChange = { _ -> onToggleAlarmOnOff() },
        thumbContent = {
          Icon(
            imageVector = if (alarm.isOn) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
          )
        },
      )
    }

    Spacer(modifier = Modifier.height(8.dp))
  }
}
