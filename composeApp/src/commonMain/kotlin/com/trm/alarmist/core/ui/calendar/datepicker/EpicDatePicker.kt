package com.trm.alarmist.core.ui.calendar.datepicker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import com.trm.alarmist.core.ui.calendar.basis.BasisDayOfMonthContent
import com.trm.alarmist.core.ui.calendar.basis.BasisDayOfWeekContent
import com.trm.alarmist.core.ui.calendar.basis.DefaultDayOfWeekContent
import com.trm.alarmist.core.ui.calendar.basis.contains
import com.trm.alarmist.core.ui.calendar.basis.state.LocalBasisEpicCalendarState
import com.trm.alarmist.core.ui.calendar.datepicker.config.LocalEpicDatePickerConfig
import com.trm.alarmist.core.ui.calendar.datepicker.state.DefaultEpicDatePickerState
import com.trm.alarmist.core.ui.calendar.datepicker.state.LocalEpicDatePickerState
import com.trm.alarmist.core.ui.calendar.pager.EpicCalendarPager
import com.trm.alarmist.core.ui.calendar.ranges.drawEpicRanges

val DefaultDayOfMonthContent: BasisDayOfMonthContent = { date ->
  val basisState = LocalBasisEpicCalendarState.current
  val pickerState = LocalEpicDatePickerState.current
  val selectedDate = pickerState.selectedDate

  Text(
    modifier = Modifier.alpha(if (date in basisState.currentMonth) 1.0f else 0.5f),
    text = date.day.toString(),
    textAlign = TextAlign.Center,
    color =
      if (date == selectedDate) pickerState.config.selectionContentColor
      else pickerState.config.pagerConfig.basisConfig.contentColor,
  )
}

@Composable
fun EpicDatePicker(
  state: DefaultEpicDatePickerState,
  modifier: Modifier = Modifier,
  dayOfWeekContent: BasisDayOfWeekContent = DefaultDayOfWeekContent,
  dayOfMonthContent: BasisDayOfMonthContent = DefaultDayOfMonthContent,
) =
  with(state.config) {
    CompositionLocalProvider(
      LocalEpicDatePickerConfig provides state.config,
      LocalEpicDatePickerState provides state,
    ) {
      val selectedDate = state.selectedDate
      EpicCalendarPager(
        modifier = modifier,
        pageModifier = {
          Modifier.drawEpicRanges(
            ranges = listOf(selectedDate..selectedDate),
            color = selectionContainerColor,
          )
        },
        state = state.pagerState,
        onDayOfMonthClick = { state.selectedDate = it },
        dayOfMonthContent = dayOfMonthContent,
        dayOfWeekContent = dayOfWeekContent,
      )
    }
  }
