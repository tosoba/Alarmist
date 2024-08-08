package com.trm.alarmist.feature.group

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.add_alarms_after_creating_group
import alarmist.composeapp.generated.resources.back
import alarmist.composeapp.generated.resources.confirm
import alarmist.composeapp.generated.resources.delete_group
import alarmist.composeapp.generated.resources.group_name
import alarmist.composeapp.generated.resources.group_name_blank_validation_error
import alarmist.composeapp.generated.resources.invalid_input
import alarmist.composeapp.generated.resources.no_alarms_created
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.elevatedIf
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.AlarmFireAtTime
import com.trm.alarmist.core.ui.AlarmFireOnDateTimeCountdown
import com.trm.alarmist.core.ui.AlarmGroupHeaderCard
import com.trm.alarmist.core.ui.AlarmScheduleDescription
import com.trm.alarmist.core.ui.BottomGradientBackground
import com.trm.alarmist.core.ui.ExpandableIcon
import com.trm.alarmist.core.ui.NoRippleInteractionSource
import com.trm.alarmist.core.ui.TopGradientBackground
import com.trm.alarmist.core.ui.floatingActionButtonSpacerItem
import com.trm.alarmist.core.ui.keyboardAsState
import com.trm.alarmist.core.ui.theme.bottomSheetBackgroundColor
import com.trm.alarmist.core.ui.theme.onOffCardColors
import com.trm.alarmist.core.ui.theme.onOffContainer
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupContent(
  modifier: Modifier = Modifier,
  component: GroupComponent,
  onDeleteActionClick: () -> Unit,
  onBackClick: () -> Unit,
  onConfirmClick: () -> Unit,
) {
  val groupState by component.feature.state.collectAsState()

  GroupContent(
    modifier = modifier,
    mode = component.mode,
    state = groupState,
    onBackClick = onBackClick,
    onNameChange = component.feature::onNameChange,
    onDeleteClick = if (component.mode is GroupComponent.Mode.Edit) onDeleteActionClick else null,
    onColorChange = component.feature::onColorChange,
    onToggleAlarmSelection = component.feature::onToggleAlarmSelection,
    onConfirmClick = onConfirmClick,
  )
}

@Composable
fun GroupContent(
  modifier: Modifier = Modifier,
  mode: GroupComponent.Mode = GroupComponent.Mode.Add,
  state: GroupState = GroupState(),
  onBackClick: () -> Unit = {},
  onNameChange: (String) -> Unit = {},
  onDeleteClick: (() -> Unit)? = null,
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
        AlarmGroupHeaderCard(
          group = group,
          onClick = { toggleGroupExpanded(group.id) },
          modifier = modifier,
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
            Box(modifier = Modifier.padding(end = 16.dp)) {
              ExpandableIcon(isExpanded = isExpanded, transitionLabel = "${group.name}Header")
            }
          },
        )
      }

      if (isExpanded) {
        val alarms = state.alarmsInGroup(group.id)
        itemsIndexed(alarms) { index, alarm ->
          Box(modifier = Modifier.fillMaxWidth()) {
            GroupedAlarmCard(
              alarm = alarm,
              modifier = Modifier.fillMaxWidth(),
              shape =
                if (index == alarms.lastIndex) {
                  ShapeDefaults.Medium.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))
                } else {
                  RectangleShape
                },
              isSelected = alarm.id in state.selectedAlarmIds,
              onToggleAlarmSelection = remember(alarm) { { onToggleAlarmSelection(alarm) } },
            )

            HorizontalDivider(
              modifier =
                Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.TopCenter)
            )
          }
        }
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
      item {
        val isKeyboardOpen by keyboardAsState()
        val focusManager = LocalFocusManager.current
        LaunchedEffect(isKeyboardOpen) { if (!isKeyboardOpen) focusManager.clearFocus() }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBackClick, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.back),
            )
          }

          OutlinedTextField(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            value = state.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(Res.string.group_name)) },
            singleLine = true,
            isError = state.blankNameError,
            supportingText = {
              AnimatedVisibility(state.blankNameError) {
                Text(stringResource(Res.string.group_name_blank_validation_error))
              }
            },
          )

          onDeleteClick?.let {
            IconButton(onClick = it, modifier = Modifier.padding(bottom = 8.dp)) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.delete_group),
              )
            }
          }
        }
      }

      item {
        GroupColors(
          modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
          selectedColor = Color(state.color),
          onColorClick = onColorChange,
        )
      }

      if (state.groups.values.all { it.alarmsCount == 0L }) {
        item { NoAlarmsCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) }
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

      floatingActionButtonSpacerItem()
    }

    val interactionSource = remember(::MutableInteractionSource)
    val noRippleInteractionSource = remember(::NoRippleInteractionSource)
    ExtendedFloatingActionButton(
      modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
      expanded = state.blankNameError,
      containerColor =
        if (state.blankNameError) MaterialTheme.colorScheme.errorContainer
        else FloatingActionButtonDefaults.containerColor,
      elevation =
        if (state.blankNameError) FloatingActionButtonDefaults.bottomAppBarFabElevation()
        else FloatingActionButtonDefaults.elevation(),
      onClick = onConfirmClick,
      icon = {
        Icon(
          imageVector = if (state.blankNameError) Icons.Default.Error else Icons.Default.Check,
          contentDescription = stringResource(Res.string.confirm),
        )
      },
      interactionSource =
        if (state.blankNameError) noRippleInteractionSource else interactionSource,
      text = {
        AnimatedVisibility(state.blankNameError) { Text(stringResource(Res.string.invalid_input)) }
      },
    )

    TopGradientBackground(color = bottomSheetBackgroundColor())
    BottomGradientBackground(color = bottomSheetBackgroundColor())
  }
}

@Composable
private fun GroupedAlarmCard(
  alarm: AlarmListModel,
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  isSelected: Boolean = false,
  onToggleAlarmSelection: () -> Unit = {},
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.onOffCardColors(alarm.isOn),
    shape = shape,
    elevation = CardDefaults.elevatedIf(alarm.isOn),
    onClick = onToggleAlarmSelection,
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    alarm.name?.let {
      Text(
        text = it,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onOffContainer(alarm.isOn),
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      AlarmFireAtTime(fireAtTime = alarm.nextFireAtTime, isOn = alarm.isOn)
      Spacer(modifier = Modifier.width(8.dp))
      Checkbox(checked = isSelected, onCheckedChange = { onToggleAlarmSelection() })
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AlarmScheduleDescription(
        isOn = alarm.isOn,
        scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek,
        scheduledOnDate = alarm.closestScheduledOnDate,
        offOnScheduledDate = alarm.offOnAllScheduledDates,
        scheduledOnMultipleDates = alarm.scheduledOnMultipleDates,
      )

      Spacer(modifier = Modifier.weight(1f))

      AlarmFireOnDateTimeCountdown(fireOnDateTime = alarm.fireOnDateTime, isOn = alarm.isOn)
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun GroupColors(
  modifier: Modifier = Modifier,
  selectedColor: Color? = null,
  onColorClick: (Color) -> Unit = {},
) {
  val boxColors = remember {
    listOf(
      Color.Transparent,
      Color.Red,
      Color.Yellow,
      Color.Green,
      Color.Cyan,
      Color.Blue,
      Color.Magenta,
      Color.Black,
    )
  }
  LazyRow(modifier = modifier) {
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
        .background(color = color, shape = RoundedCornerShape(16.dp))
        .border(
          width = 0.5.dp,
          color = MaterialTheme.colorScheme.onBackground,
          shape = RoundedCornerShape(16.dp),
        )
        .clip(RoundedCornerShape(16.dp))
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

@Composable
private fun NoAlarmsCard(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      modifier = Modifier.size(100.dp),
      imageVector = Icons.Default.AlarmOff,
      contentDescription = stringResource(Res.string.no_alarms_created),
    )

    Spacer(Modifier.height(16.dp))

    Text(
      text = stringResource(Res.string.no_alarms_created),
      style = MaterialTheme.typography.headlineMedium,
      textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(8.dp))

    Text(
      text = stringResource(Res.string.add_alarms_after_creating_group),
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center,
    )
  }
}
