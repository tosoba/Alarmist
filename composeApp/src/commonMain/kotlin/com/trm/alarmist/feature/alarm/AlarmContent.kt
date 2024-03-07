package com.trm.alarmist.feature.alarm

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.name
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.ui.DayOfWeekEllipsizedContent
import com.trm.alarmist.core.ui.ExpandableIcon
import com.trm.alarmist.core.ui.WheelTimePicker
import com.trm.alarmist.core.ui.keyboardAsState
import epicarchitect.calendar.compose.basis.EpicMonth
import epicarchitect.calendar.compose.basis.config.rememberMutableBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.contains
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.LocalEpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AlarmContent(
  modifier: Modifier = Modifier,
  state: AlarmState = AlarmState(),
  onNameChange: (String) -> Unit = {},
  onFireAtChange: (LocalTime) -> Unit = {},
  onDayOfWeekClick: (DayOfWeek) -> Unit = {},
  onDateOnOffSwitchCheckedChange: (Boolean, LocalDate) -> Unit = { _, _ -> },
  onDeleteOnAllDaysWeekClick: (DayOfWeek) -> Unit = {},
  onDeleteOnDateClick: (LocalDate) -> Unit = {},
  onScheduleOnDateClick: (LocalDate) -> Unit = {},
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

      ElevatedCard(
        modifier =
          Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
      ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
          Text(text = "Fire at time:")

          val textStyle = MaterialTheme.typography.headlineMedium
          val textHeightDp = with(LocalDensity.current) { textStyle.fontSize.toDp() } + 10.dp
          WheelTimePicker(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            startTime = state.fireAtTime,
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
          Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            text = "Scheduled on:",
          )
          // TODO: add some extra description about when exactly alarm is going to fire that
          // will change as user tweaks scheduled on settings

          DaysOfWeekRow(
            modifier =
              Modifier.fillMaxWidth().padding(8.dp).horizontalScroll(rememberScrollState()),
            selectedDaysOfWeek = state.scheduledOnDaysOfWeek,
            onDayOfWeekClick = onDayOfWeekClick,
          )

          var isCalendarExpanded by remember { mutableStateOf(false) }
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .clickable { isCalendarExpanded = !isCalendarExpanded }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text("Calendar", modifier = Modifier.padding(horizontal = 8.dp))
            ExpandableIcon(isExpanded = isCalendarExpanded, transitionLabel = "ExpandableCalendar")
          }
          ExpandableCalendar(
            calendarModifier = Modifier.fillMaxWidth(),
            fireAtTime = state.fireAtTime,
            isExpanded = isCalendarExpanded,
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

      ElevatedCard(
        modifier =
          Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
      ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
          Text("Settings:")
          // TODO: sound/volume/vibrate options/snooze duration/delete button in edit mode
          // (marked
          // in red) + maybe group choice?
        }
      }

      Spacer(modifier = Modifier.height(72.dp))
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

@OptIn(ExperimentalFoundationApi::class)
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
  val scope = rememberCoroutineScope()

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

      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
          modifier = Modifier.padding(start = 16.dp),
          text =
            "${state.pagerState.currentMonth.month.name.lowercase().capitalize(Locale.current)} ${state.pagerState.currentMonth.year}",
        ) // TODO: copy over year selection expandable menu from material DatePicker

        Spacer(Modifier.weight(1f))

        IconButton(
          enabled = state.pagerState.currentMonth > state.pagerState.monthRange.start,
          onClick = { scope.launch { state.pagerState.scrollMonths(-1) } },
        ) {
          Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }

        IconButton(
          enabled = state.pagerState.currentMonth < state.pagerState.monthRange.endInclusive,
          onClick = { scope.launch { state.pagerState.scrollMonths(1) } },
        ) {
          Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
      }

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
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)
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
                  Text("Delete on all ${selectedDate.dayOfWeek.name.lowercase()}s")
                }
              }
              selectedDate in scheduledOnDates -> {
                DateOnOffSwitch(selectedDate)
                TextButton(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = { onDeleteOnDateClick(selectedDate) },
                ) {
                  Text("Delete")
                }
              }
              else -> {
                TextButton(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = { onScheduleOnDateClick(selectedDate) },
                ) {
                  Text("Schedule alarm")
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
    Text(modifier = Modifier.padding(end = 8.dp), text = "Scheduled")
    Spacer(modifier = Modifier.weight(1f))
    Text(if (isOn) "On" else "Off")
    Switch(
      modifier = Modifier.padding(start = 8.dp),
      checked = isOn,
      onCheckedChange = onCheckedChange,
      thumbContent = {
        Icon(
          imageVector = if (isOn) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = null,
        )
      },
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
