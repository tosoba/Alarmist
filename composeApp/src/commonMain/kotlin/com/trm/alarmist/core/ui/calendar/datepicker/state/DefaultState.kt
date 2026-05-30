package com.trm.alarmist.core.ui.calendar.datepicker.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import com.trm.alarmist.core.ui.calendar.datepicker.config.EpicDatePickerConfig
import com.trm.alarmist.core.ui.calendar.datepicker.config.LocalEpicDatePickerConfig
import com.trm.alarmist.core.ui.calendar.pager.state.EpicCalendarPagerState
import com.trm.alarmist.core.ui.calendar.pager.state.defaultEpicCalendarPagerMonthRange
import com.trm.alarmist.core.ui.calendar.pager.state.rememberEpicCalendarPagerState
import kotlinx.datetime.LocalDate

@Stable
class DefaultEpicDatePickerState(
  config: EpicDatePickerConfig,
  selectedDate: LocalDate,
  val pagerState: EpicCalendarPagerState,
) {
  var config by mutableStateOf(config)
  var selectedDate by mutableStateOf(selectedDate)
}

@Stable
@Composable
fun rememberEpicDatePickerState(
  selectedDate: LocalDate,
  config: EpicDatePickerConfig = LocalEpicDatePickerConfig.current,
  monthRange: ClosedRange<EpicMonth> = defaultEpicCalendarPagerMonthRange(),
  initialMonth: EpicMonth = EpicMonth.now(),
): DefaultEpicDatePickerState {
  val pagerState =
    rememberEpicCalendarPagerState(
      config = config.pagerConfig,
      monthRange = monthRange,
      initialMonth = initialMonth,
    )
  return remember(config, selectedDate, pagerState) {
    DefaultEpicDatePickerState(
      config = config,
      selectedDate = selectedDate,
      pagerState = pagerState,
    )
  }
}
