package com.trm.alarmist.core.ui.calendar.datepicker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import com.trm.alarmist.core.ui.calendar.basis.BasisDayOfMonthContent
import com.trm.alarmist.core.ui.calendar.basis.BasisDayOfWeekContent
import com.trm.alarmist.core.ui.calendar.basis.DefaultDayOfWeekContent
import com.trm.alarmist.core.ui.calendar.basis.contains
import com.trm.alarmist.core.ui.calendar.basis.state.LocalBasisEpicCalendarState
import com.trm.alarmist.core.ui.calendar.datepicker.config.LocalEpicDatePickerConfig
import com.trm.alarmist.core.ui.calendar.datepicker.state.EpicDatePickerState
import com.trm.alarmist.core.ui.calendar.datepicker.state.LocalEpicDatePickerState
import com.trm.alarmist.core.ui.calendar.datepicker.state.rememberEpicDatePickerState
import com.trm.alarmist.core.ui.calendar.pager.EpicCalendarPager
import com.trm.alarmist.core.ui.calendar.ranges.drawEpicRanges

val DefaultDayOfMonthContent: BasisDayOfMonthContent = { date ->
  val basisState = LocalBasisEpicCalendarState.current!!
  val pickerState = LocalEpicDatePickerState.current!!
  val selectedDays = pickerState.selectedDates
  val selectionMode = pickerState.selectionMode

  val isSelected =
    remember(selectionMode, selectedDays, date) {
      when (selectionMode) {
        is EpicDatePickerState.SelectionMode.Range -> {
          if (selectedDays.isEmpty()) false else date in selectedDays.min()..selectedDays.max()
        }

        is EpicDatePickerState.SelectionMode.Single -> date in selectedDays
      }
    }

  Text(
    modifier = Modifier.alpha(if (date in basisState.currentMonth) 1.0f else 0.5f),
    text = date.dayOfMonth.toString(),
    textAlign = TextAlign.Center,
    color =
      if (isSelected) pickerState.config.selectionContentColor
      else pickerState.config.pagerConfig.basisConfig.contentColor,
  )
}

@Composable
fun EpicDatePicker(
  modifier: Modifier = Modifier,
  state: EpicDatePickerState = LocalEpicDatePickerState.current ?: rememberEpicDatePickerState(),
  dayOfWeekContent: BasisDayOfWeekContent = DefaultDayOfWeekContent,
  dayOfMonthContent: BasisDayOfMonthContent = DefaultDayOfMonthContent,
) =
  with(state.config) {
    CompositionLocalProvider(
      LocalEpicDatePickerConfig provides state.config,
      LocalEpicDatePickerState provides state,
    ) {
      val mode = state.selectionMode
      val selectedDays = state.selectedDates
      val ranges =
        remember(mode, selectedDays) {
          when (mode) {
            is EpicDatePickerState.SelectionMode.Range -> {
              if (selectedDays.isEmpty()) emptyList()
              else listOf(selectedDays.min()..selectedDays.max())
            }

            is EpicDatePickerState.SelectionMode.Single -> {
              selectedDays.map { it..it }
            }
          }
        }

      EpicCalendarPager(
        modifier = modifier,
        pageModifier = {
          Modifier.drawEpicRanges(ranges = ranges, color = selectionContainerColor)
        },
        state = state.pagerState,
        onDayOfMonthClick = state::toggleDateSelection,
        dayOfMonthContent = dayOfMonthContent,
        dayOfWeekContent = dayOfWeekContent,
      )
    }
  }
