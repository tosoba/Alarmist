package com.trm.alarmist.feature.alarms.upcoming

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.style.TextAlign
import com.trm.alarmist.core.common.util.nextDayOfWeek
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.previousDayOfWeek
import com.trm.alarmist.core.ui.DatePickerYearMonthControls
import com.trm.alarmist.core.ui.DayOfWeekEllipsizedContent
import com.trm.alarmist.core.ui.DaysOfWeekLabelsRow
import com.trm.alarmist.core.ui.DaysOfWeekRow
import com.trm.alarmist.core.ui.WeekArrowsRow
import epicarchitect.calendar.compose.basis.EpicMonth
import epicarchitect.calendar.compose.basis.config.rememberMutableBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.contains
import epicarchitect.calendar.compose.basis.firstDayOfWeek
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.datepicker.EpicDatePicker
import epicarchitect.calendar.compose.datepicker.config.LocalEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.EpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.LocalEpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus

@Composable
fun UpcomingAlarmsContent(modifier: Modifier = Modifier, initialState: UpcomingAlarmsState) {
  LazyColumn(modifier = modifier) { item { WeeklyMonthlyCalendar(initialState) } }
}

@Composable
private fun rememberMonthlyCalendarState(initialState: UpcomingAlarmsState): EpicDatePickerState =
  rememberEpicDatePickerState(
    config =
      rememberEpicDatePickerConfig(
        pagerConfig =
          rememberEpicCalendarPagerConfig(basisConfig = rememberMutableBasisEpicCalendarConfig()),
        selectionContentColor = MaterialTheme.colorScheme.onPrimary,
        selectionContainerColor = MaterialTheme.colorScheme.primary,
      ),
    monthRange = EpicMonth.now()..EpicMonth(2100, Month.DECEMBER),
    initialMonth = EpicMonth(initialState.currentYear, initialState.currentMonth),
    selectedDates = listOfNotNull(initialState.selectedDate),
  )

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WeeklyMonthlyCalendar(
  initialState: UpcomingAlarmsState,
  modifier: Modifier = Modifier,
) {
  val today = LocalDate.now()
  val scope = rememberCoroutineScope()

  val weeklyCalendarState =
    rememberPagerState(
      initialPage =
        (LocalDate(initialState.currentYear, initialState.currentMonth, 1)
            .previousDayOfWeek(firstDayOfWeek())
            .toEpochDays() - today.previousDayOfWeek(firstDayOfWeek()).toEpochDays())
          .coerceAtLeast(0) / 7
    ) {
      weeklyCalendarPagesCount(startDate = today, endDate = LocalDate(2100, Month.DECEMBER, 31))
    }

  val monthlyCalendarState = rememberMonthlyCalendarState(initialState)

  var calendarMode by rememberSaveable { mutableStateOf(CalendarMode.WEEKLY) }

  LaunchedEffect(weeklyCalendarState.currentPage) {
    if (calendarMode == CalendarMode.WEEKLY) {
      monthlyCalendarState.pagerState.scrollToMonth(
        weeklyCalendarRowDates(today, weeklyCalendarState.currentPage)
          .run {
            monthlyCalendarState.selectedDates.firstOrNull()?.takeIf { it in this } ?: first()
          }
          .run { EpicMonth(year, month) }
      )
    }
  }

  LaunchedEffect(monthlyCalendarState.pagerState.currentMonth) {
    if (calendarMode == CalendarMode.MONTHLY) {
      val destinationDate =
        monthlyCalendarState.selectedDates.firstOrNull()?.takeIf {
          it.month == monthlyCalendarState.pagerState.currentMonth.month
        } ?: with(monthlyCalendarState.pagerState.currentMonth) { LocalDate(year, month, 1) }
      weeklyCalendarState.scrollToPage(
        (destinationDate.previousDayOfWeek(firstDayOfWeek()).toEpochDays() -
            today.previousDayOfWeek(firstDayOfWeek()).toEpochDays())
          .coerceAtLeast(0) / 7
      )
    }
  }

  Crossfade(targetState = calendarMode, modifier = modifier) { mode ->
    Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
      when (mode) {
        CalendarMode.WEEKLY -> {
          CompositionLocalProvider(
            LocalEpicDatePickerConfig provides monthlyCalendarState.config,
            LocalEpicDatePickerState provides monthlyCalendarState,
          ) {
            WeekArrowsRow(
              rowDates = weeklyCalendarRowDates(today, weeklyCalendarState.currentPage),
              modifier = Modifier.fillMaxWidth(),
              prevWeekEnabled = weeklyCalendarState.canScrollBackward,
              onPrevWeekClick = {
                scope.launch {
                  weeklyCalendarState.animateScrollToPage(weeklyCalendarState.currentPage - 1)
                }
              },
              nextWeekEnabled = weeklyCalendarState.canScrollForward,
              onNextWeekClick = {
                scope.launch {
                  weeklyCalendarState.animateScrollToPage(weeklyCalendarState.currentPage + 1)
                }
              },
            )

            HorizontalPager(state = weeklyCalendarState, modifier = Modifier.fillMaxWidth()) {
              pageIndex ->
              Column(modifier = Modifier.fillMaxWidth()) {
                DaysOfWeekLabelsRow(modifier = Modifier.fillMaxWidth())
                DaysOfWeekRow(
                  rowDates = weeklyCalendarRowDates(today, pageIndex),
                  modifier = Modifier.fillMaxWidth(),
                  selectedDates = monthlyCalendarState.selectedDates,
                  onDayOfMonthClick = monthlyCalendarState::toggleDateSelection,
                )
              }
            }
          }

          TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { calendarMode = CalendarMode.MONTHLY },
          ) {
            Text("Expand calendar")
          }
        }
        CalendarMode.MONTHLY -> {
          DatePickerYearMonthControls(
            pagerState = monthlyCalendarState.pagerState,
            modifier = Modifier.fillMaxWidth(),
          )

          EpicDatePicker(
            state = monthlyCalendarState,
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

          TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { calendarMode = CalendarMode.WEEKLY },
          ) {
            Text("Collapse calendar")
          }
        }
      }
    }
  }
}

private enum class CalendarMode {
  WEEKLY,
  MONTHLY
}

private fun weeklyCalendarPagesCount(startDate: LocalDate, endDate: LocalDate): Int {
  require(endDate > startDate)
  val startDateFirstDayOfWeek = startDate.previousDayOfWeek(firstDayOfWeek())
  val lastDayOfWeek = startDateFirstDayOfWeek.plus(6, DateTimeUnit.DAY).dayOfWeek
  return (endDate.nextDayOfWeek(lastDayOfWeek).toEpochDays() -
    startDateFirstDayOfWeek.toEpochDays() + 1) / DayOfWeek.entries.size
}

private fun weeklyCalendarRowDates(today: LocalDate, weekIndex: Int): List<LocalDate> {
  require(weekIndex >= 0)
  val startDate = today.previousDayOfWeek(firstDayOfWeek()).plus(weekIndex * 7, DateTimeUnit.DAY)
  return List(7) { startDate.plus(it, DateTimeUnit.DAY) }
}
