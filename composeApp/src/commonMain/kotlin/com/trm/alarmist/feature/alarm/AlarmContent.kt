package com.trm.alarmist.feature.alarm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.ui.WheelTimePicker
import epicarchitect.calendar.compose.basis.config.rememberMutableBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.contains
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.EpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.LocalEpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@Composable
fun AlarmContent(
  modifier: Modifier = Modifier,
  state: AlarmState,
  onFireAtChange: (LocalTime) -> Unit,
  onDayOfWeekClick: (DayOfWeek) -> Unit,
  onToggleCalendarExpandedClick: () -> Unit,
  onConfirmClick: () -> Unit,
) {
  Box(modifier = modifier) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      ElevatedCard(
        modifier =
          Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(text = "Fire at time:")

          val textStyle = MaterialTheme.typography.headlineMedium
          val textHeightDp = with(LocalDensity.current) { textStyle.fontSize.toDp() } + 10.dp
          WheelTimePicker(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            startTime = state.fireAt,
            rowCount = 5,
            size = DpSize(textHeightDp, textHeightDp) * 5,
            textStyle = textStyle,
            centerTextStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            onSnappedTime = onFireAtChange,
          )
        }
      }

      ElevatedCard(
        modifier =
          Modifier.fillMaxWidth()
            .animateContentSize()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
      ) {
        Column {
          Text(modifier = Modifier.padding(horizontal = 8.dp), text = "Scheduled on:")
          // TODO: add some extra description about when exactly alarm is going to fire that will
          // change as user tweaks scheduled on settings

          DaysOfWeekRow(
            modifier =
              Modifier.fillMaxWidth().padding(8.dp).horizontalScroll(rememberScrollState()),
            selectedDaysOfWeek = state.selectedDaysOfWeek,
            onDayOfWeekClick = onDayOfWeekClick,
          )

          ExpandableCalendar(
            headerModifier =
              Modifier.fillMaxWidth()
                .clickable(onClick = onToggleCalendarExpandedClick)
                .padding(vertical = 8.dp),
            calendarModifier = Modifier.fillMaxWidth(),
            isExpanded = state.isCalendarExpanded,
          )
        }
      }

      ElevatedCard(
        modifier =
          Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text("Settings:")
          // TODO: sound/volume/vibrate options/snooze duration/delete button in edit mode (marked
          // in red) + maybe group choice?
        }
      }
    }

    var permissionDialogVisible by rememberSaveable { mutableStateOf(false) }
    var shouldShowRationale by rememberSaveable { mutableStateOf(false) }

    val permissionsHandler =
      alarmPermissionsHandler(
        onDenied = {
          shouldShowRationale = it
          permissionDialogVisible = true
        },
        onGranted = onConfirmClick,
      )

    PostNotificationPermissionInfoDialog(
      visible = permissionDialogVisible,
      text =
        if (shouldShowRationale) {
          "A permission to post notifications is required to create an alarm. Permission dialog will appear again after clicking OK."
        } else {
          "A permission to post notifications which you have denied is required to create an alarm. Use application settings to grant that permission."
        },
      onDismiss = { permissionDialogVisible = false },
      onOkClick = {
        permissionDialogVisible = false
        if (shouldShowRationale) {
          permissionsHandler()
        }
      },
    )

    FloatingActionButton(
      modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
      onClick = permissionsHandler,
    ) {
      Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
    }
  }
}

@Composable
private fun ColumnScope.ExpandableCalendar(
  headerModifier: Modifier = Modifier,
  calendarModifier: Modifier = Modifier,
  isExpanded: Boolean,
) {
  Row(
    modifier = headerModifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(modifier = Modifier.padding(horizontal = 8.dp), text = "Calendar:")
    val expandedTransition =
      updateTransition(
        targetState = isExpanded,
        label = "updateTransition-ExpandableCalendar-isExpanded",
      )
    val expandImageRotation by
      expandedTransition.animateFloat(label = "animateFloat-ExpandableCalendar-rotation") { state ->
        if (state) 180f else 0f
      }
    Image(
      modifier = Modifier.rotate(expandImageRotation),
      imageVector = Icons.Default.ArrowDropDown,
      contentDescription = null,
    )
  }

  val basisConfig = rememberMutableBasisEpicCalendarConfig()
  val state =
    rememberEpicDatePickerState(
      config =
        rememberEpicDatePickerConfig(
          pagerConfig = rememberEpicCalendarPagerConfig(basisConfig = basisConfig),
          selectionContentColor = MaterialTheme.colorScheme.onPrimary,
          selectionContainerColor = MaterialTheme.colorScheme.primary,
        )
    )
  AnimatedVisibility(modifier = calendarModifier, visible = isExpanded) {
    EpicDatePicker(
      state = state,
      dayOfMonthContent = { date ->
        val basisState = LocalBasisEpicCalendarState.current!!
        val pickerState = LocalEpicDatePickerState.current!!
        val selectedDays = pickerState.selectedDates
        val selectionMode = pickerState.selectionMode

        val isSelected =
          remember(selectionMode, selectedDays, date) {
            when (selectionMode) {
              is EpicDatePickerState.SelectionMode.Range -> {
                if (selectedDays.isEmpty()) false
                else date in selectedDays.min()..selectedDays.max()
              }
              is EpicDatePickerState.SelectionMode.Single -> date in selectedDays
            }
          }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            modifier = Modifier.alpha(if (date in basisState.currentMonth) 1.0f else 0.5f),
            text = date.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            color =
              if (isSelected) pickerState.config.selectionContentColor
              else pickerState.config.pagerConfig.basisConfig.contentColor,
          )
          if (date in basisState.currentMonth) {
            Box(Modifier.size(5.dp).clip(CircleShape).background(Color.Red))
          }
        }
      },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DaysOfWeekRow(
  modifier: Modifier = Modifier,
  selectedDaysOfWeek: Collection<DayOfWeek> = emptyList(),
  onDayOfWeekClick: (DayOfWeek) -> Unit = {},
) {
  Row(modifier = modifier) {
    DayOfWeek.entries.forEach { dayOfWeek ->
      val isSelected = selectedDaysOfWeek.contains(dayOfWeek)
      Card(
        modifier = Modifier.padding(horizontal = 8.dp),
        onClick = { onDayOfWeekClick(dayOfWeek) },
        elevation =
          if (isSelected) {
            CardDefaults.cardElevation(
              defaultElevation = 0.dp,
              pressedElevation = 0.dp,
              focusedElevation = 0.dp,
            )
          } else {
            CardDefaults.cardElevation(
              defaultElevation = 1.dp,
              pressedElevation = 1.dp,
              focusedElevation = 1.dp,
            )
          },
        colors =
          if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
          } else {
            CardDefaults.cardColors()
          },
      ) {
        Text(
          modifier = Modifier.padding(8.dp),
          text = dayOfWeek.name.take(2),
          fontWeight = if (isSelected) FontWeight.SemiBold else null,
        )
      }
    }
  }
}

@Composable
private fun PostNotificationPermissionInfoDialog(
  modifier: Modifier = Modifier,
  visible: Boolean,
  text: String,
  onOkClick: () -> Unit,
  onDismiss: () -> Unit,
) {
  AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
    AlertDialog(
      modifier = modifier,
      onDismissRequest = onDismiss,
      confirmButton = { TextButton(onClick = onOkClick) { Text(text = "OK") } },
      dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancel") } },
      title = { Text(text = "Permission required", textAlign = TextAlign.Center) },
      text = { Text(text = text) },
    )
  }
}
