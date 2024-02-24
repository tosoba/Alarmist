package com.trm.alarmist.feature.group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.ExpandableHeaderRow
import com.trm.alarmist.core.ui.keyboardAsState

@Composable
fun GroupContent(
  modifier: Modifier = Modifier,
  mode: GroupComponent.Mode = GroupComponent.Mode.Add,
  state: GroupState = GroupState(),
  onNameChange: (String) -> Unit = {},
  onColorChange: (Color) -> Unit = {},
  onToggleAlarmSelection: (AlarmListModel) -> Unit = {},
  onConfirmClick: () -> Unit = {},
) {
  Box(modifier = modifier) {
    val groupsExpandedState =
      remember(state.groups, state.alarms) {
        mutableStateMapOf<Long, Boolean>().apply { putAll(state.groups.mapValues { false }) }
      }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
      item {
        val isKeyboardOpen by keyboardAsState()
        val focusManager = LocalFocusManager.current
        LaunchedEffect(isKeyboardOpen) { if (!isKeyboardOpen) focusManager.clearFocus() }

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          value = state.name,
          onValueChange = onNameChange,
          label = { Text("Name") },
          singleLine = true,
        )
      }

      item {
        GroupColors(
          modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
          selectedColor = Color(state.color),
          onColorClick = onColorChange,
        )
      }

      if (mode is GroupComponent.Mode.Edit) {
        // TODO: alarms in group (only in edit mode)
      }

      if ((state.groups[AlarmGroupModel.UNGROUPED_ID]?.alarmsCount ?: 0L) > 0L) {
        item {
          ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape =
              if (groupsExpandedState[AlarmGroupModel.UNGROUPED_ID] == true) {
                ShapeDefaults.Medium.copy(
                  bottomStart = CornerSize(0.dp),
                  bottomEnd = CornerSize(0.dp),
                )
              } else {
                ShapeDefaults.Medium
              },
          ) {
            ExpandableHeaderRow(
              modifier =
                Modifier.fillMaxWidth().clickable {
                  groupsExpandedState[AlarmGroupModel.UNGROUPED_ID] =
                    groupsExpandedState[AlarmGroupModel.UNGROUPED_ID]?.not() ?: false
                },
              isExpanded = groupsExpandedState[AlarmGroupModel.UNGROUPED_ID] ?: false,
              text = AlarmGroupModel.UNGROUPED_NAME,
              transitionLabel = "UngroupedHeader",
            )
          }
        }
        if (groupsExpandedState[AlarmGroupModel.UNGROUPED_ID] == true) {
          itemsIndexed(state.ungroupedAlarms) { index, alarm ->
            Card(
              modifier = modifier,
              shape =
                if (index == state.ungroupedAlarms.lastIndex) {
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
            ) {
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = alarm.fireAtTime.toString(),
                  style =
                    MaterialTheme.typography.headlineMedium.run {
                      if (alarm.isOn) copy(fontWeight = FontWeight.Medium) else this
                    },
                )

                alarm.name?.let {
                  Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = it,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                  )
                }

                Spacer(modifier = Modifier.weight(1f))

                Checkbox(
                  checked = alarm.id in state.selectedAlarmIds,
                  onCheckedChange = remember(alarm) { { onToggleAlarmSelection(alarm) } },
                )
              }

              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }
      }

      // TODO: alarms in other groups that can be moved (?)
    }

    FloatingActionButton(
      modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
      onClick = onConfirmClick,
    ) {
      Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
    }
  }
}

@Composable
private fun GroupColors(
  modifier: Modifier = Modifier,
  selectedColor: Color? = null,
  onColorClick: (Color) -> Unit,
) {
  val boxColors = remember {
    listOf(Color.Transparent, Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
  }
  LazyRow(modifier = modifier) {
    // TODO: add a gradient circle to show a color picker dialog to pick a custom color
    items(boxColors) { color ->
      GroupColor(
        color = color,
        isSelected = color == selectedColor,
        onClick = remember(color) { { onColorClick(color) } },
      )
      Spacer(modifier = Modifier.width(16.dp))
    }
  }
}

@Composable
private fun GroupColor(color: Color, isSelected: Boolean, onClick: () -> Unit) {
  Box(
    modifier =
      Modifier.size(64.dp)
        .background(color = color, shape = CircleShape)
        .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)
        .clip(CircleShape)
        .clickable(onClick = onClick)
  ) {
    if (isSelected) {
      Icon(
        modifier = Modifier.align(Alignment.Center),
        imageVector = Icons.Default.Check,
        contentDescription = null,
      )
    }
  }
}
