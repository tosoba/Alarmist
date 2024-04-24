package com.trm.alarmist.feature.alarm

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarm_duration_label
import alarmist.composeapp.generated.resources.cancel
import alarmist.composeapp.generated.resources.confirm
import alarmist.composeapp.generated.resources.custom_schedule_label
import alarmist.composeapp.generated.resources.delete
import alarmist.composeapp.generated.resources.delete_all_weekdays
import alarmist.composeapp.generated.resources.fire_at_time_label
import alarmist.composeapp.generated.resources.groups_label
import alarmist.composeapp.generated.resources.name
import alarmist.composeapp.generated.resources.notification_permission_rationale
import alarmist.composeapp.generated.resources.notification_permission_settings
import alarmist.composeapp.generated.resources.ok
import alarmist.composeapp.generated.resources.paused
import alarmist.composeapp.generated.resources.permission_required
import alarmist.composeapp.generated.resources.reminder_offset_label
import alarmist.composeapp.generated.resources.repeat_on_label
import alarmist.composeapp.generated.resources.schedule_alarm
import alarmist.composeapp.generated.resources.scheduled
import alarmist.composeapp.generated.resources.snooze_duration_label
import alarmist.composeapp.generated.resources.snooze_limit_label
import alarmist.composeapp.generated.resources.sound_enabled_label
import alarmist.composeapp.generated.resources.time_dial
import alarmist.composeapp.generated.resources.time_input
import alarmist.composeapp.generated.resources.vibration_enabled_label
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.ui.AlarmGroupHeaderCard
import com.trm.alarmist.core.ui.DatePickerYearMonthControls
import com.trm.alarmist.core.ui.DayOfWeekEllipsizedContent
import com.trm.alarmist.core.ui.ExpandableIcon
import com.trm.alarmist.core.ui.FloatingActionButtonSpacer
import com.trm.alarmist.core.ui.keyboardAsState
import com.trm.alarmist.core.ui.theme.onOffCardColors
import epicarchitect.calendar.compose.basis.EpicMonth
import epicarchitect.calendar.compose.basis.config.rememberMutableBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.contains
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.LocalEpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlarmContent(
  modifier: Modifier = Modifier,
  state: AlarmState = AlarmState(),
  groups: List<AlarmGroupModel> = emptyList(),
  onNameChange: (String) -> Unit = {},
  onFireAtChange: (LocalTime) -> Unit = {},
  onDayOfWeekClick: (DayOfWeek) -> Unit = {},
  onDateOnOffSwitchCheckedChange: (Boolean, LocalDate) -> Unit = { _, _ -> },
  onDeleteOnAllDaysWeekClick: (DayOfWeek) -> Unit = {},
  onDeleteOnDateClick: (LocalDate) -> Unit = {},
  onScheduleOnDateClick: (LocalDate) -> Unit = {},
  onSnoozeDurationChange: (AlarmSnoozeDuration) -> Unit = {},
  onSnoozeLimitChange: (Long) -> Unit = {},
  onAlarmDurationChange: (Long) -> Unit = {},
  onToggleSoundEnabled: () -> Unit = {},
  onToggleVibrationEnabled: () -> Unit = {},
  onReminderOffsetChange: (AlarmReminderOffset) -> Unit = {},
  onGroupClick: (AlarmGroupModel) -> Unit = {},
  onConfirmClick: () -> Unit = {},
) {
  Box(modifier = modifier) {
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      val isKeyboardOpen by keyboardAsState()
      val focusManager = LocalFocusManager.current
      LaunchedEffect(isKeyboardOpen) { if (!isKeyboardOpen) focusManager.clearFocus() }

      OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        value = state.name.orEmpty(),
        onValueChange = onNameChange,
        label = { Text(stringResource(Res.string.name)) },
        singleLine = true,
      )

      OutlinedCard(
        modifier =
          Modifier.fillMaxWidth()
            .animateContentSize()
            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
      ) {
        Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
          Text(
            text = stringResource(Res.string.fire_at_time_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )

          var timePickerMode by rememberSaveable { mutableStateOf(TimePickerMode.DIAL) }

          Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            val timePickerState =
              rememberTimePickerState(
                initialHour = state.fireAtTime.hour,
                initialMinute = state.fireAtTime.minute,
              )
            LaunchedEffect(timePickerState.hour, timePickerState.minute) {
              onFireAtChange(LocalTime(timePickerState.hour, timePickerState.minute))
            }
            Crossfade(timePickerMode, modifier = Modifier.align(Alignment.Center)) {
              when (it) {
                TimePickerMode.DIAL -> TimePicker(state = timePickerState)
                TimePickerMode.INPUT -> TimeInput(state = timePickerState)
              }
            }
          }

          IconButton(
            onClick = {
              timePickerMode =
                when (timePickerMode) {
                  TimePickerMode.DIAL -> TimePickerMode.INPUT
                  TimePickerMode.INPUT -> TimePickerMode.DIAL
                }
            }
          ) {
            Crossfade(timePickerMode) {
              when (it) {
                TimePickerMode.DIAL -> {
                  Icon(
                    imageVector = Icons.Outlined.Keyboard,
                    contentDescription = stringResource(Res.string.time_input),
                  )
                }
                TimePickerMode.INPUT -> {
                  Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = stringResource(Res.string.time_dial),
                  )
                }
              }
            }
          }
        }
      }

      OutlinedCard(
        modifier =
          Modifier.fillMaxWidth()
            .animateContentSize()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
      ) {
        Column {
          Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(Res.string.repeat_on_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )

          DaysOfWeekRow(
            modifier =
              Modifier.fillMaxWidth().padding(8.dp).horizontalScroll(rememberScrollState()),
            selectedDaysOfWeek = state.scheduledOnDaysOfWeek,
            onDayOfWeekClick = onDayOfWeekClick,
          )

          var isCustomScheduleExpanded by remember { mutableStateOf(false) }
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .clickable { isCustomScheduleExpanded = !isCustomScheduleExpanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = stringResource(Res.string.custom_schedule_label),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.width(16.dp))
            ExpandableIcon(
              isExpanded = isCustomScheduleExpanded,
              transitionLabel = "ExpandableCustomSchedule",
            )
          }
          ExpandableCalendar(
            calendarModifier = Modifier.fillMaxWidth(),
            fireAtTime = state.fireAtTime,
            isExpanded = isCustomScheduleExpanded,
            scheduledOnDaysOfWeek = state.scheduledOnDaysOfWeek,
            scheduledOnDates = state.scheduledOnDates,
            offOnDates = state.offOnDates,
            onDateOnOffSwitchCheckedChange = onDateOnOffSwitchCheckedChange,
            onDeleteOnAllDaysWeekClick = onDeleteOnAllDaysWeekClick,
            onDeleteOnDateClick = onDeleteOnDateClick,
            onScheduleOnDateClick = onScheduleOnDateClick,
          )
        }
      }

      OutlinedCard(
        modifier =
          Modifier.fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .animateContentSize()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.sound_enabled_label))
            Spacer(Modifier.weight(1f))
            Switch(
              checked = state.soundEnabled,
              onCheckedChange = remember { { onToggleSoundEnabled() } },
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.vibration_enabled_label))
            Spacer(Modifier.weight(1f))
            Switch(
              checked = state.vibrationEnabled,
              onCheckedChange = remember { { onToggleVibrationEnabled() } },
            )
          }

          Spacer(Modifier.height(8.dp))

          Text(
            text = stringResource(Res.string.alarm_duration_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )

          Slider(
            value = state.alarmDuration.toFloat(),
            valueRange =
              AlarmState.MIN_ALARM_DURATION_MINUTES.toFloat()..AlarmState.MAX_ALARM_DURATION_MINUTES
                  .toFloat(),
            steps =
              AlarmState.MAX_ALARM_DURATION_MINUTES.toInt() -
                AlarmState.MIN_ALARM_DURATION_MINUTES.toInt(),
            onValueChange = { onAlarmDurationChange(it.toLong()) },
            thumb = { AlarmSliderThumb(text = state.alarmDuration.toString()) },
            modifier = Modifier.fillMaxWidth(),
          )

          Text(
            text = stringResource(Res.string.snooze_duration_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )

          val snoozeDurationValues = remember { AlarmSnoozeDuration.entries.toTypedArray() }
          Slider(
            value = state.snoozeDuration.ordinal.toFloat(),
            valueRange = 0f..snoozeDurationValues.lastIndex.toFloat(),
            onValueChange = { onSnoozeDurationChange(snoozeDurationValues[it.toInt()]) },
            thumb = { AlarmSliderThumb(text = state.snoozeDuration.minutes.toString()) },
            modifier = Modifier.fillMaxWidth(),
          )

          AnimatedVisibility(
            visible = state.snoozeDuration != AlarmSnoozeDuration.ZERO,
            enter = fadeIn(),
            exit = fadeOut(),
          ) {
            Column {
              Text(
                text = stringResource(Res.string.snooze_limit_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )

              Slider(
                value = state.snoozeLimit.toFloat(),
                valueRange =
                  AlarmState.MIN_SNOOZE_LIMIT.toFloat()..AlarmState.MAX_SNOOZE_LIMIT.toFloat(),
                steps = AlarmState.MAX_SNOOZE_LIMIT.toInt() - AlarmState.MIN_SNOOZE_LIMIT.toInt(),
                onValueChange = { onSnoozeLimitChange(it.toLong()) },
                thumb = { AlarmSliderThumb(text = state.snoozeLimit.toString()) },
                modifier = Modifier.fillMaxWidth(),
              )
            }
          }

          Spacer(Modifier.height(8.dp))

          Text(
            text = stringResource(Res.string.reminder_offset_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )

          val reminderOffsetValues = remember { AlarmReminderOffset.entries.toTypedArray() }
          Slider(
            value = state.reminderOffset.ordinal.toFloat(),
            valueRange = 0f..reminderOffsetValues.lastIndex.toFloat(),
            onValueChange = { onReminderOffsetChange(reminderOffsetValues[it.toInt()]) },
            thumb = { AlarmSliderThumb(text = state.reminderOffset.hours.toString()) },
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }

      if (groups.isNotEmpty()) {
        OutlinedCard(
          modifier =
            Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
          Column {
            Text(
              text = stringResource(Res.string.groups_label),
              modifier = Modifier.padding(16.dp),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            groups.forEachIndexed { index, group ->
              val shape =
                when (index) {
                  0 -> {
                    ShapeDefaults.Medium.copy(
                      bottomStart = CornerSize(0.dp),
                      bottomEnd = CornerSize(0.dp),
                    )
                  }
                  groups.lastIndex -> {
                    ShapeDefaults.Medium.copy(
                      topStart = CornerSize(0.dp),
                      topEnd = CornerSize(0.dp),
                    )
                  }
                  else -> {
                    RectangleShape
                  }
                }

              Box(modifier = Modifier.fillMaxWidth()) {
                AlarmGroupHeaderCard(
                  group = group,
                  modifier = Modifier.fillMaxWidth().clip(shape).clickable { onGroupClick(group) },
                  shape = shape,
                  trailing = {
                    Checkbox(
                      checked = state.groupId == group.id,
                      onCheckedChange = { onGroupClick(group) },
                      modifier = Modifier.padding(end = 8.dp),
                    )
                  },
                )

                if (index != 0) {
                  HorizontalDivider(
                    modifier =
                      Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.TopCenter)
                  )
                }
              }
            }
          }
        }
      }

      FloatingActionButtonSpacer()
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
          stringResource(Res.string.notification_permission_rationale)
        } else {
          stringResource(Res.string.notification_permission_settings)
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
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = stringResource(Res.string.confirm),
      )
    }
  }
}

@Composable
private fun AlarmSliderThumb(text: String, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    SliderDefaults.Thumb(
      interactionSource = remember(::MutableInteractionSource),
      colors = SliderDefaults.colors(),
      enabled = true,
      modifier = Modifier.align(Alignment.Center),
    )
    Text(
      text = text,
      fontSize = 12.sp,
      color = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier.align(Alignment.Center),
    )
  }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
private fun ColumnScope.ExpandableCalendar(
  calendarModifier: Modifier = Modifier,
  fireAtTime: LocalTime = LocalTime.now(),
  isExpanded: Boolean = false,
  scheduledOnDaysOfWeek: Set<DayOfWeek> = emptySet(),
  scheduledOnDates: Set<LocalDate> = emptySet(),
  offOnDates: Set<LocalDate> = emptySet(),
  onDateOnOffSwitchCheckedChange: (Boolean, LocalDate) -> Unit = { _, _ -> },
  onDeleteOnAllDaysWeekClick: (DayOfWeek) -> Unit = {},
  onDeleteOnDateClick: (LocalDate) -> Unit = {},
  onScheduleOnDateClick: (LocalDate) -> Unit = {},
) {
  AnimatedVisibility(modifier = calendarModifier, visible = isExpanded) {
    Column {
      val state =
        rememberEpicDatePickerState(
          config =
            rememberEpicDatePickerConfig(
              pagerConfig =
                rememberEpicCalendarPagerConfig(
                  basisConfig = rememberMutableBasisEpicCalendarConfig()
                ),
              selectionContentColor = MaterialTheme.colorScheme.onPrimary,
              selectionContainerColor = MaterialTheme.colorScheme.primary,
            ),
          monthRange = EpicMonth.now()..EpicMonth(2100, Month.DECEMBER),
        )

      DatePickerYearMonthControls(pagerState = state.pagerState, modifier = Modifier.fillMaxWidth())

      EpicDatePicker(
        state = state,
        dayOfWeekContent = DayOfWeekEllipsizedContent,
        dayOfMonthContent = { date ->
          val basisState = LocalBasisEpicCalendarState.current!!
          val pickerState = LocalEpicDatePickerState.current!!

          val selectedDays = pickerState.selectedDates
          val isSelected = remember(selectedDays, date) { date in selectedDays }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              modifier =
                Modifier.alpha(
                  when {
                    date < LocalDate.now() -> 0.5f
                    date in basisState.currentMonth -> 1.0f
                    else -> 0.5f
                  }
                ),
              text = date.dayOfMonth.toString(),
              textAlign = TextAlign.Center,
              color =
                if (isSelected) pickerState.config.selectionContentColor
                else pickerState.config.pagerConfig.basisConfig.contentColor,
            )

            if (
              date > LocalDate.now() || (date == LocalDate.now() && fireAtTime > LocalTime.now())
            ) {
              if (date in offOnDates) {
                Box(
                  Modifier.size(7.dp)
                    .clip(CircleShape)
                    .border(width = 0.5.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
              } else if (date.dayOfWeek in scheduledOnDaysOfWeek || date in scheduledOnDates) {
                Box(
                  Modifier.size(7.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
                )
              }
            }
          }
        },
      )

      val bringIntoViewRequester = remember(::BringIntoViewRequester)
      var layoutRect: Rect? by remember { mutableStateOf(null) }

      LaunchedEffect(state.selectedDates) {
        if (state.selectedDates.isNotEmpty()) {
          bringIntoViewRequester.bringIntoView(layoutRect)
        }
      }

      @Composable
      fun DateOnOffSwitch(date: LocalDate) {
        CalendarDateAlarmOnOffSwitch(
          modifier = Modifier.fillMaxWidth(),
          isOn = date !in offOnDates,
          onCheckedChange = { isChecked -> onDateOnOffSwitchCheckedChange(isChecked, date) },
        )
      }

      state.selectedDates
        .firstOrNull()
        ?.takeIf { selectedDate ->
          selectedDate > LocalDate.now() ||
            (selectedDate == LocalDate.now() && fireAtTime > LocalTime.now())
        }
        ?.let { selectedDate ->
          val selectedDateAlarmsLayoutExtraHeightPx = with(LocalDensity.current) { 72.dp.toPx() }
          Column(
            modifier =
              Modifier.padding(horizontal = 16.dp)
                .bringIntoViewRequester(bringIntoViewRequester)
                .onGloballyPositioned {
                  layoutRect =
                    it.size
                      .toSize()
                      .run { copy(height = height + selectedDateAlarmsLayoutExtraHeightPx) }
                      .toRect()
                }
          ) {
            when {
              selectedDate.dayOfWeek in scheduledOnDaysOfWeek -> {
                DateOnOffSwitch(selectedDate)
                TextButton(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = { onDeleteOnAllDaysWeekClick(selectedDate.dayOfWeek) },
                ) {
                  Text(
                    stringResource(
                      Res.string.delete_all_weekdays,
                      selectedDate.dayOfWeek.name.lowercase(),
                    )
                  )
                }
              }
              selectedDate in scheduledOnDates -> {
                DateOnOffSwitch(selectedDate)
                TextButton(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = { onDeleteOnDateClick(selectedDate) },
                ) {
                  Text(stringResource(Res.string.delete))
                }
              }
              else -> {
                TextButton(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = { onScheduleOnDateClick(selectedDate) },
                ) {
                  Text(stringResource(Res.string.schedule_alarm))
                }
              }
            }
          }
        }
    }
  }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun CalendarDateAlarmOnOffSwitch(
  modifier: Modifier = Modifier,
  isOn: Boolean = false,
  onCheckedChange: (Boolean) -> Unit = {},
) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(
      modifier = Modifier.padding(end = 8.dp),
      text = stringResource(if (isOn) Res.string.scheduled else Res.string.paused),
    )
    Spacer(modifier = Modifier.weight(1f))
    Switch(
      modifier = Modifier.padding(start = 8.dp),
      checked = isOn,
      onCheckedChange = onCheckedChange,
    )
  }
}

@Composable
private fun DaysOfWeekRow(
  modifier: Modifier = Modifier,
  selectedDaysOfWeek: Collection<DayOfWeek> = emptyList(),
  onDayOfWeekClick: (DayOfWeek) -> Unit = {},
) {
  Row(modifier = modifier) {
    DayOfWeek.entries.forEach { dayOfWeek ->
      val isSelected = dayOfWeek in selectedDaysOfWeek
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
        colors = CardDefaults.onOffCardColors(isSelected),
      ) {
        Text(
          modifier = Modifier.padding(12.dp),
          text = dayOfWeek.name.take(2),
          fontWeight = if (isSelected) FontWeight.SemiBold else null,
          style = MaterialTheme.typography.headlineSmall,
        )
      }
    }
  }
}

@OptIn(ExperimentalResourceApi::class)
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
      confirmButton = {
        TextButton(onClick = onOkClick) { Text(text = stringResource(Res.string.ok)) }
      },
      dismissButton = {
        TextButton(onClick = onDismiss) { Text(text = stringResource(Res.string.cancel)) }
      },
      title = {
        Text(text = stringResource(Res.string.permission_required), textAlign = TextAlign.Center)
      },
      text = { Text(text = text) },
    )
  }
}

private enum class TimePickerMode {
  DIAL,
  INPUT
}
