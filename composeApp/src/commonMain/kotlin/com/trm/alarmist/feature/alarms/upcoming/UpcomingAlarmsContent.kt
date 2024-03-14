package com.trm.alarmist.feature.alarms.upcoming

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trm.alarmist.core.common.util.nextDayOfWeek
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.previousDayOfWeek
import com.trm.alarmist.core.ui.DaysOfWeekLabelsRow
import com.trm.alarmist.core.ui.DaysOfWeekRow
import com.trm.alarmist.core.ui.WeekArrowsRow
import epicarchitect.calendar.compose.basis.EpicMonth
import epicarchitect.calendar.compose.basis.config.rememberMutableBasisEpicCalendarConfig
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpcomingAlarmsContent(modifier: Modifier = Modifier, component: UpcomingAlarmsComponent) {
  val today = LocalDate.now()
  val scope = rememberCoroutineScope()

  Column(modifier = modifier) {
    val weeklyCalendarPagerState = rememberPagerState {
      weeklyCalendarPagesCount(startDate = today, endDate = LocalDate(2100, Month.DECEMBER, 31))
    }

    WeekArrowsRow(
      rowDates = weeklyCalendarRowDates(today, weeklyCalendarPagerState.currentPage),
      modifier = Modifier.fillMaxWidth(),
      prevWeekEnabled = weeklyCalendarPagerState.canScrollBackward,
      onPrevWeekClick = {
        scope.launch {
          weeklyCalendarPagerState.animateScrollToPage(weeklyCalendarPagerState.currentPage - 1)
        }
      },
      nextWeekEnabled = weeklyCalendarPagerState.canScrollForward,
      onNextWeekClick = {
        scope.launch {
          weeklyCalendarPagerState.animateScrollToPage(weeklyCalendarPagerState.currentPage + 1)
        }
      },
    )

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
    // TODO: CrossFade with EpicCalendar + draw selectedDates in DaysOfWeekRow
    HorizontalPager(state = weeklyCalendarPagerState, modifier = Modifier.fillMaxWidth()) {
      pageIndex ->
      Column(modifier = Modifier.fillMaxWidth()) {
        DaysOfWeekLabelsRow(modifier = Modifier.fillMaxWidth())
        DaysOfWeekRow(
          rowDates = weeklyCalendarRowDates(today, pageIndex),
          modifier = Modifier.fillMaxWidth(),
          selectedDates = state.selectedDates,
        )
      }
    }
  }
  Box(modifier = modifier) { Text("Upcoming", modifier = Modifier.align(Alignment.Center)) }
}

private fun weeklyCalendarPagesCount(startDate: LocalDate, endDate: LocalDate): Int {
  require(endDate > startDate)
  return (endDate.nextDayOfWeek(DayOfWeek.SATURDAY).toEpochDays() -
    startDate.previousDayOfWeek(DayOfWeek.SUNDAY).toEpochDays() + 1) / DayOfWeek.entries.size
}

private fun weeklyCalendarRowDates(today: LocalDate, weekIndex: Int): List<LocalDate> {
  require(weekIndex >= 0)
  val startDate = today.previousDayOfWeek(DayOfWeek.SUNDAY).plus(weekIndex * 7, DateTimeUnit.DAY)
  return List(7) { startDate.plus(it, DateTimeUnit.DAY) }
}
