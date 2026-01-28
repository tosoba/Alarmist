package com.trm.alarmist.feature.alarm

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarm_name
import alarmist.composeapp.generated.resources.auto_schedule_label
import alarmist.composeapp.generated.resources.back
import alarmist.composeapp.generated.resources.confirm
import alarmist.composeapp.generated.resources.custom_schedule_label
import alarmist.composeapp.generated.resources.delete
import alarmist.composeapp.generated.resources.delete_alarm
import alarmist.composeapp.generated.resources.delete_all_weekdays
import alarmist.composeapp.generated.resources.duration_label
import alarmist.composeapp.generated.resources.edit_fire_at_time
import alarmist.composeapp.generated.resources.fire_at_label
import alarmist.composeapp.generated.resources.group_label
import alarmist.composeapp.generated.resources.hours_before_alarm_label
import alarmist.composeapp.generated.resources.minutes_label
import alarmist.composeapp.generated.resources.paused
import alarmist.composeapp.generated.resources.reminder_label
import alarmist.composeapp.generated.resources.repeat_weekly_label
import alarmist.composeapp.generated.resources.schedule_alarm
import alarmist.composeapp.generated.resources.scheduled
import alarmist.composeapp.generated.resources.sound_label
import alarmist.composeapp.generated.resources.vibration_label
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.IncompleteCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.trm.alarmist.core.common.util.nextFullHour
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.ui.AlarmFireAtTime
import com.trm.alarmist.core.ui.AlarmGroupHeaderCard
import com.trm.alarmist.core.ui.BottomGradientBackground
import com.trm.alarmist.core.ui.DatePickerYearMonthControls
import com.trm.alarmist.core.ui.DayOfWeekEllipsizedContent
import com.trm.alarmist.core.ui.ExpandableIcon
import com.trm.alarmist.core.ui.FloatingActionButtonSpacer
import com.trm.alarmist.core.ui.TopGradientBackground
import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import com.trm.alarmist.core.ui.calendar.basis.config.rememberMutableBasisEpicCalendarConfig
import com.trm.alarmist.core.ui.calendar.basis.contains
import com.trm.alarmist.core.ui.calendar.basis.state.LocalBasisEpicCalendarState
import com.trm.alarmist.core.ui.calendar.datepicker.EpicDatePicker
import com.trm.alarmist.core.ui.calendar.datepicker.config.rememberEpicDatePickerConfig
import com.trm.alarmist.core.ui.calendar.datepicker.state.LocalEpicDatePickerState
import com.trm.alarmist.core.ui.calendar.datepicker.state.rememberEpicDatePickerState
import com.trm.alarmist.core.ui.calendar.pager.config.rememberEpicCalendarPagerConfig
import com.trm.alarmist.core.ui.keyboardAsState
import com.trm.alarmist.core.ui.theme.onOffCardColors
import com.trm.alarmist.feature.alarm.model.AlarmReminderOffset
import com.trm.alarmist.feature.alarm.model.AlarmState
import com.trm.alarmist.feature.alarm.sound.AlarmSoundDialog
import com.trm.alarmist.feature.alarm.sound.alarmSoundTitle
import com.trm.alarmist.feature.alarm.time.AlarmTimeDialog
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlarmContent(
  modifier: Modifier = Modifier,
  component: AlarmComponent,
  onDeleteActionClick: () -> Unit,
  onBackClick: () -> Unit,
  onCallScrollBackwardChange: (Boolean) -> Unit,
  onConfirmClick: () -> Unit,
) {
  val alarmState by component.feature.state.collectAsState()
  val groups by component.feature.groups.collectAsState()
  val dialog by component.dialog.subscribeAsState()

  AlarmContent(
    modifier = modifier,
    dialog = dialog,
    state = alarmState,
    groups = groups,
    onBackClick = onBackClick,
    onCallScrollBackwardChange = onCallScrollBackwardChange,
    onDeleteClick = if (component.mode is AlarmComponent.Mode.Edit) onDeleteActionClick else null,
    onNameChange = component.feature::onNameChange,
    onFireAtClick = component::onFireAtTimeClick,
    onToggleIsOnChange = component.feature::onToggleIsOnChange,
    onDayOfWeekClick = component.feature::onDayOfWeekClick,
    onDateOnOffSwitchCheckedChange = component.feature::onDateOnOffSwitchCheckedChange,
    onDeleteOnAllDaysWeekClick = component.feature::onDeleteOnAllDaysWeekClick,
    onDeleteOnDateClick = component.feature::onDeleteOnDateClick,
    onScheduleOnDateClick = component.feature::onScheduleOnDateClick,
    onAlarmDurationChange = component.feature::onAlarmDurationChange,
    onSoundClick = component::onSoundClick,
    onToggleSoundEnabled = component.feature::onToggleSoundEnabled,
    onToggleVibrationEnabled = component.feature::onToggleVibrationEnabled,
    onToggleReminderEnabled = component.feature::onToggleReminderEnabled,
    onReminderOffsetChange = component.feature::onReminderOffsetChange,
    onGroupClick = component.feature::onGroupClick,
    onConfirmClick = onConfirmClick,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmContent(
  modifier: Modifier = Modifier,
  dialog: ChildSlot<*, AlarmDialogChild>,
  state: AlarmState = AlarmState(),
  groups: List<AlarmGroupModel> = emptyList(),
  onBackClick: () -> Unit = {},
  onCallScrollBackwardChange: (Boolean) -> Unit = {},
  onDeleteClick: (() -> Unit)? = null,
  onNameChange: (String) -> Unit = {},
  onFireAtClick: () -> Unit = {},
  onToggleIsOnChange: () -> Unit = {},
  onDayOfWeekClick: (DayOfWeek) -> Unit = {},
  onDateOnOffSwitchCheckedChange: (Boolean, LocalDate) -> Unit = { _, _ -> },
  onDeleteOnAllDaysWeekClick: (DayOfWeek) -> Unit = {},
  onDeleteOnDateClick: (LocalDate) -> Unit = {},
  onScheduleOnDateClick: (LocalDate) -> Unit = {},
  onAlarmDurationChange: (Long) -> Unit = {},
  onSoundClick: () -> Unit = {},
  onToggleSoundEnabled: () -> Unit = {},
  onToggleVibrationEnabled: () -> Unit = {},
  onToggleReminderEnabled: () -> Unit = {},
  onReminderOffsetChange: (AlarmReminderOffset) -> Unit = {},
  onGroupClick: (AlarmGroupModel) -> Unit = {},
  onConfirmClick: () -> Unit = {},
) {
  Box(modifier = modifier) {
    val scrollState = rememberScrollState()
    val canScrollBackward by remember { derivedStateOf(scrollState::canScrollBackward) }
    LaunchedEffect(canScrollBackward) { onCallScrollBackwardChange(canScrollBackward) }

    Column(modifier = Modifier.fillMaxSize().animateContentSize().verticalScroll(scrollState)) {
      val isKeyboardOpen by keyboardAsState()
      val focusManager = LocalFocusManager.current
      LaunchedEffect(isKeyboardOpen) { if (!isKeyboardOpen) focusManager.clearFocus() }

      Row(
        modifier =
          Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp, bottom = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = onBackClick, modifier = Modifier.padding(top = 8.dp)) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.back),
          )
        }

        OutlinedTextField(
          value = state.name.orEmpty(),
          onValueChange = onNameChange,
          label = { Text(stringResource(Res.string.alarm_name)) },
          singleLine = true,
          modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
        )

        onDeleteClick?.let {
          IconButton(onClick = it, modifier = Modifier.padding(top = 8.dp)) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = stringResource(Res.string.delete_alarm),
            )
          }
        } ?: run { Spacer(modifier = Modifier.width(16.dp)) }
      }

      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onFireAtClick)
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Alarm,
              contentDescription = stringResource(Res.string.fire_at_label),
            )

            Text(
              text = stringResource(Res.string.fire_at_label),
              style = MaterialTheme.typography.titleLarge,
              modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            )
          }

          Spacer(modifier = Modifier.height(24.dp))

          state.fireAtTime?.let { AlarmFireAtTime(fireAtTime = it, isOn = true) }
        }

        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = stringResource(Res.string.edit_fire_at_time),
        )
      }

      ToggleableSwitchRow(
        value = state.isOn,
        label = stringResource(Res.string.auto_schedule_label),
        imageVector = Icons.Default.AlarmOn,
        onValueChange = remember { { onToggleIsOnChange() } },
      )

      Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Icon(
          imageVector = Icons.Default.Repeat,
          contentDescription = stringResource(Res.string.repeat_weekly_label),
          modifier = Modifier.padding(end = 12.dp),
        )

        Text(
          text = stringResource(Res.string.repeat_weekly_label),
          style = MaterialTheme.typography.titleLarge,
        )
      }

      DaysOfWeekRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp).horizontalScroll(rememberScrollState()),
        selectedDaysOfWeek = state.scheduledOnDaysOfWeek,
        onDayOfWeekClick = onDayOfWeekClick,
      )

      var isCustomScheduleExpanded by remember { mutableStateOf(false) }
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { isCustomScheduleExpanded = !isCustomScheduleExpanded }
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.EditCalendar,
          contentDescription = stringResource(Res.string.custom_schedule_label),
          modifier = Modifier.padding(end = 12.dp),
        )

        Text(
          text = stringResource(Res.string.custom_schedule_label),
          style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.heightIn(min = 24.dp).padding(start = 12.dp)) {
          ExpandableIcon(
            isExpanded = isCustomScheduleExpanded,
            modifier = Modifier.align(Alignment.Center),
            transitionLabel = "ExpandableCustomSchedule",
          )
        }
      }

      ExpandableCalendar(
        calendarModifier =
          Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp, bottom = 16.dp),
        fireAtTime = state.fireAtTime ?: remember { LocalTime(now().nextFullHour(), 0) },
        isExpanded = isCustomScheduleExpanded,
        scheduledOnDaysOfWeek = state.scheduledOnDaysOfWeek,
        scheduledOnDates = state.scheduledOnDates,
        offOnDates = state.offOnDates,
        onDateOnOffSwitchCheckedChange = onDateOnOffSwitchCheckedChange,
        onDeleteOnAllDaysWeekClick = onDeleteOnAllDaysWeekClick,
        onDeleteOnDateClick = onDeleteOnDateClick,
        onScheduleOnDateClick = onScheduleOnDateClick,
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 24.dp),
      ) {
        Row(
          modifier =
            Modifier.weight(1f)
              .clip(RoundedCornerShape(24.dp))
              .clickable(onClick = onSoundClick)
              .padding(start = 24.dp, top = 16.dp, bottom = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = stringResource(Res.string.sound_label),
            modifier = Modifier.padding(end = 12.dp),
          )

          Column {
            Text(
              text = stringResource(Res.string.sound_label),
              style = MaterialTheme.typography.titleLarge,
            )
            Text(text = alarmSoundTitle(state.soundId), style = MaterialTheme.typography.bodyMedium)
          }
        }

        VerticalDivider(modifier = Modifier.height(32.dp).padding(start = 8.dp, end = 16.dp))

        Switch(
          checked = state.soundEnabled,
          onCheckedChange = remember { { onToggleSoundEnabled() } },
        )
      }

      ToggleableSwitchRow(
        value = state.vibrationEnabled,
        label = stringResource(Res.string.vibration_label),
        imageVector = Icons.Default.Vibration,
        onValueChange = remember { { onToggleVibrationEnabled() } },
      )

      Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.AvTimer,
          contentDescription = stringResource(Res.string.duration_label),
          modifier = Modifier.padding(end = 12.dp),
        )

        Column {
          Text(
            text = stringResource(Res.string.duration_label),
            style = MaterialTheme.typography.titleLarge,
          )
          Text(
            text = stringResource(Res.string.minutes_label, state.alarmDuration),
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }

      Slider(
        value = state.alarmDuration.toFloat(),
        valueRange =
          AlarmState.MIN_ALARM_DURATION_MINUTES.toFloat()..AlarmState.MAX_ALARM_DURATION_MINUTES
              .toFloat(),
        steps =
          AlarmState.MAX_ALARM_DURATION_MINUTES.toInt() -
            AlarmState.MIN_ALARM_DURATION_MINUTES.toInt() -
            1,
        onValueChange = { onAlarmDurationChange(it.toLong()) },
        thumb = { AlarmSliderThumb(text = state.alarmDuration.toString()) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
      )

      ToggleableSwitchRow(
        value = state.reminderEnabled,
        label = stringResource(Res.string.reminder_label),
        imageVector = Icons.Default.IncompleteCircle,
        onValueChange = remember { { onToggleReminderEnabled() } },
      ) {
        Column {
          Text(
            text = stringResource(Res.string.reminder_label),
            style = MaterialTheme.typography.titleLarge,
          )

          AnimatedVisibility(visible = state.reminderEnabled) {
            Text(
              text =
                stringResource(Res.string.hours_before_alarm_label, state.reminderOffset.hours),
              style = MaterialTheme.typography.bodyMedium,
            )
          }
        }
      }

      AnimatedVisibility(visible = state.reminderEnabled, enter = fadeIn(), exit = fadeOut()) {
        val reminderOffsetValues = remember { AlarmReminderOffset.entries.toTypedArray() }
        Slider(
          value = state.reminderOffset.ordinal.toFloat(),
          valueRange = 0f..reminderOffsetValues.lastIndex.toFloat(),
          onValueChange = { onReminderOffsetChange(reminderOffsetValues[it.toInt()]) },
          thumb = { AlarmSliderThumb(text = state.reminderOffset.hours.toString()) },
          modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        )
      }

      if (groups.isNotEmpty()) {
        Text(
          text = stringResource(Res.string.group_label),
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )

        groups.forEachIndexed { index, group ->
          val shape =
            when (index) {
              0 -> {
                if (groups.size > 1) {
                  ShapeDefaults.Medium.copy(
                    bottomStart = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp),
                  )
                } else {
                  ShapeDefaults.Medium
                }
              }
              groups.lastIndex -> {
                if (groups.size > 1) {
                  ShapeDefaults.Medium.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))
                } else {
                  ShapeDefaults.Medium
                }
              }
              else -> {
                RectangleShape
              }
            }

          Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            AlarmGroupHeaderCard(
              group = group,
              onClick = { onGroupClick(group) },
              modifier = Modifier.fillMaxWidth(),
              shape = shape,
              trailing = {
                Checkbox(
                  checked = state.groupId == group.id,
                  onCheckedChange = { onGroupClick(group) },
                  modifier = Modifier.padding(end = 16.dp),
                )
              },
            )

            if (index != 0) {
              HorizontalDivider(
                modifier =
                  Modifier.fillMaxWidth().padding(horizontal = 24.dp).align(Alignment.TopCenter)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
        }
      }

      FloatingActionButtonSpacer()
    }

    dialog.child?.instance?.let {
      when (it) {
        is AlarmDialogChild.Sound -> {
          AlarmSoundDialog(component = it.component, modifier = Modifier.heightIn(max = 500.dp))
        }
        is AlarmDialogChild.Time -> {
          AlarmTimeDialog(component = it.component)
        }
      }
    }

    FloatingActionButton(
      modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
      onClick = postNotificationsPermissionHandler(onGranted = onConfirmClick),
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = stringResource(Res.string.confirm),
      )
    }

    TopGradientBackground(color = BottomSheetDefaults.ContainerColor)
    BottomGradientBackground(color = BottomSheetDefaults.ContainerColor)
  }
}

@Composable
private fun ToggleableSwitchRow(
  value: Boolean,
  label: String,
  imageVector: ImageVector,
  onValueChange: (Boolean) -> Unit,
  content: @Composable () -> Unit = {
    Text(text = label, style = MaterialTheme.typography.titleLarge)
  },
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier =
      Modifier.clip(RoundedCornerShape(24.dp))
        .toggleable(value = value, role = Role.Switch, onValueChange = onValueChange)
        .padding(horizontal = 24.dp, vertical = 16.dp)
        .semantics { contentDescription = label },
  ) {
    Icon(
      imageVector = imageVector,
      contentDescription = null,
      modifier = Modifier.padding(end = 12.dp),
    )

    content()

    Spacer(Modifier.weight(1f))

    Switch(checked = value, onCheckedChange = null)
  }
}

@Composable
private fun AlarmSliderThumb(text: String, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    SliderDefaults.Thumb(
      interactionSource = remember(::MutableInteractionSource),
      enabled = true,
      modifier = Modifier.align(Alignment.Center),
    )

    Box(
      modifier =
        Modifier.size(24.dp)
          .align(Alignment.Center)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.background)
          .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape)
    ) {
      Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.align(Alignment.Center),
      )
    }
  }
}

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
              text = date.day.toString(),
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
              Modifier.padding(horizontal = 24.dp)
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

@Composable
private fun CalendarDateAlarmOnOffSwitch(
  modifier: Modifier = Modifier,
  isOn: Boolean = false,
  onCheckedChange: (Boolean) -> Unit = {},
) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(
      modifier = Modifier.padding(end = 16.dp),
      text = stringResource(if (isOn) Res.string.scheduled else Res.string.paused),
    )
    Spacer(modifier = Modifier.weight(1f))
    Switch(
      modifier = Modifier.padding(start = 16.dp),
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
              defaultElevation = 1.dp,
              pressedElevation = 1.dp,
              focusedElevation = 1.dp,
            )
          } else {
            CardDefaults.cardElevation(
              defaultElevation = 0.dp,
              pressedElevation = 0.dp,
              focusedElevation = 0.dp,
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
