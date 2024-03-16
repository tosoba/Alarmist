package com.trm.alarmist.feature.alarms.upcoming

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import com.trm.alarmist.core.common.util.nextDayOfWeek
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.previousDayOfWeek
import com.trm.alarmist.core.ui.DayOfWeekEllipsizedContent
import com.trm.alarmist.core.ui.DaysOfWeekLabelsRow
import com.trm.alarmist.core.ui.DaysOfWeekRow
import com.trm.alarmist.core.ui.WeekArrowsRow
import epicarchitect.calendar.compose.basis.EpicMonth
import epicarchitect.calendar.compose.basis.config.rememberMutableBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.contains
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.config.LocalEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.LocalEpicDatePickerState
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

    Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
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

      var calendarExpanded by remember { mutableStateOf(false) }
      Crossfade(calendarExpanded) { expanded ->
        if (expanded) {
          Column(modifier = Modifier.fillMaxWidth()) {
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
                }
              },
            )
            TextButton(modifier = Modifier.fillMaxWidth(), onClick = { calendarExpanded = false }) {
              Text("Collapse calendar")
            }
          }
        } else {
          CompositionLocalProvider(
            LocalEpicDatePickerConfig provides state.config,
            LocalEpicDatePickerState provides state,
          ) {
            HorizontalPager(state = weeklyCalendarPagerState, modifier = Modifier.fillMaxWidth()) {
              pageIndex ->
              Column(modifier = Modifier.fillMaxWidth()) {
                DaysOfWeekLabelsRow(modifier = Modifier.fillMaxWidth())
                DaysOfWeekRow(
                  rowDates = weeklyCalendarRowDates(today, pageIndex),
                  modifier = Modifier.fillMaxWidth(),
                  selectedDates = state.selectedDates,
                  onDayOfMonthClick = state::toggleDateSelection,
                )
                TextButton(
                  modifier = Modifier.fillMaxWidth(),
                  onClick = { calendarExpanded = true },
                ) {
                  Text("Expand calendar")
                }
              }
            }
          }
        }
      }
    }
  }
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
