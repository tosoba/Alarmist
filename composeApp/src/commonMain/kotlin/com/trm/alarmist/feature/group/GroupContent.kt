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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.ExpandableAlarmGroupHeaderCard
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

    fun toggleGroupExpanded(id: Long) {
      groupsExpandedState[id] = groupsExpandedState[id]?.not() ?: false
    }

    fun LazyListScope.expandableAlarmsGroupItems(
      group: AlarmGroupModel,
      modifier: Modifier = Modifier,
    ) {
      if (group.alarmsCount == 0L) return

      val isExpanded = groupsExpandedState[group.id] == true
      item {
        ExpandableAlarmGroupHeaderCard(
          group = group,
          modifier = modifier,
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
          onToggleExpandedClick = ::toggleGroupExpanded,
        )
      }

      if (isExpanded) {
        val alarms = state.alarmsInGroup(group.id)
        itemsIndexed(alarms) { index, alarm ->
          GroupedAlarmCard(
            alarm = alarm,
            modifier = Modifier.fillMaxWidth(),
            shape =
              if (index == alarms.lastIndex) {
                ShapeDefaults.Medium.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))
              } else {
                RectangleShape
              },
            colors =
              if (alarm.id in state.selectedAlarmIds) {
                CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
              } else {
                CardDefaults.cardColors()
              },
            isSelected = alarm.id in state.selectedAlarmIds,
            onToggleAlarmSelection = remember { { onToggleAlarmSelection(alarm) } },
          )
        }
      }
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
        state.groups[mode.group.id]?.let {
          expandableAlarmsGroupItems(
            group = it,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
          )
        }
      }

      state.groups[AlarmGroupModel.UNGROUPED_ID]?.let {
        expandableAlarmsGroupItems(
          group = it,
          modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
      }

      state.groups.forEach { (id, group) ->
        if (id == AlarmGroupModel.UNGROUPED_ID) return@forEach
        if (mode is GroupComponent.Mode.Edit && id == mode.group.id) return@forEach
        expandableAlarmsGroupItems(
          group = group,
          modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
      }
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
fun GroupedAlarmCard(
  alarm: AlarmListModel,
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  colors: CardColors = CardDefaults.cardColors(),
  isSelected: Boolean = false,
  onToggleAlarmSelection: () -> Unit = {},
) {
  Card(modifier = modifier, shape = shape, colors = colors, onClick = onToggleAlarmSelection) {
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = alarm.fireAtTime.toString(),
        style =
          MaterialTheme.typography.headlineMedium.run {
            if (isSelected) copy(fontWeight = FontWeight.Medium) else this
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

      Checkbox(checked = isSelected, onCheckedChange = { onToggleAlarmSelection() })
    }

    Spacer(modifier = Modifier.height(8.dp))
  }
}

@Composable
private fun GroupColors(
  modifier: Modifier = Modifier,
  selectedColor: Color? = null,
  onColorClick: (Color) -> Unit = {},
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
