package com.trm.alarmist.feature.alarms.upcoming

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.collapse
import alarmist.composeapp.generated.resources.create_alarm_using_button
import alarmist.composeapp.generated.resources.expand
import alarmist.composeapp.generated.resources.no_upcoming_alarms
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.nextDayOfWeek
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.previousDayOfWeek
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.ui.DatePickerYearMonthControls
import com.trm.alarmist.core.ui.DayOfWeekEllipsizedContent
import com.trm.alarmist.core.ui.DaysOfWeekLabelsRow
import com.trm.alarmist.core.ui.DaysOfWeekRow
import com.trm.alarmist.core.ui.UpcomingAlarmListItem
import com.trm.alarmist.core.ui.WeekArrowsRow
import com.trm.alarmist.core.ui.floatingActionButtonSpacerItem
import com.trm.alarmist.feature.alarm.AlarmPermissionStatusCard
import com.trm.alarmist.feature.alarm.isAlarmPermissionGranted
import epicarchitect.calendar.compose.basis.EpicCalendarConstants
import epicarchitect.calendar.compose.basis.EpicMonth
import epicarchitect.calendar.compose.basis.atDay
import epicarchitect.calendar.compose.basis.config.BasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.config.rememberMutableBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.contains
import epicarchitect.calendar.compose.basis.firstDayOfWeek
import epicarchitect.calendar.compose.basis.lastDayOfWeek
import epicarchitect.calendar.compose.basis.localized
import epicarchitect.calendar.compose.basis.next
import epicarchitect.calendar.compose.basis.previous
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.datepicker.config.LocalEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.config.rememberEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.EpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.LocalEpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.rememberEpicDatePickerState
import epicarchitect.calendar.compose.pager.EpicCalendarPager
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import epicarchitect.calendar.compose.ranges.drawEpicRanges
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun UpcomingAlarmsContent(
  initialState: UpcomingAlarmsCalendarState,
  modifier: Modifier = Modifier,
  alarmCounts: Map<LocalDate, Int> = emptyMap(),
  selectedDateAlarms: List<UpcomingAlarmListModel> = emptyList(),
  groups: Map<Long, AlarmGroupModel> = emptyMap(),
  onAlarmItemClick: (UpcomingAlarmListModel) -> Unit = {},
  onOffButtonClick: (UpcomingAlarmListModel) -> Unit = {},
  onOffOnDateButtonClick: (UpcomingAlarmListModel) -> Unit = {},
  onOnButtonClick: (UpcomingAlarmListModel) -> Unit = {},
  onSelectedDateChange: (LocalDate?) -> Unit = {},
  onMonthlyDateRangeChange: (ClosedRange<LocalDate>) -> Unit = {},
) {
  val alarmPermissionGranted = isAlarmPermissionGranted()

  LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
  ) {
    item {
      WeeklyMonthlyCalendar(
        initialState = initialState,
        alarmCounts = alarmCounts,
        onSelectedDateChange = onSelectedDateChange,
        onMonthlyDateRangeChange = onMonthlyDateRangeChange,
      )
    }

    if (selectedDateAlarms.isEmpty()) {
      item {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .padding(vertical = 32.dp, horizontal = 16.dp)
              .animateItemPlacement(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Icon(
            modifier = Modifier.size(100.dp),
            imageVector = Icons.Default.EditCalendar,
            contentDescription = stringResource(Res.string.no_upcoming_alarms),
          )

          Spacer(Modifier.height(16.dp))

          Text(
            text = stringResource(Res.string.no_upcoming_alarms),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
          )

          Spacer(Modifier.height(8.dp))

          Text(
            text = stringResource(Res.string.create_alarm_using_button),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
          )
        }
      }
    } else if (!alarmPermissionGranted) {
      item {
        AlarmPermissionStatusCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
      }
    }

    items(selectedDateAlarms) { alarm ->
      UpcomingAlarmListItem(
        item = alarm,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).animateItemPlacement(),
        group = alarm.groupId?.let(groups::get),
        onItemClick = onAlarmItemClick,
        onOffButtonClick = onOffButtonClick,
        onOffOnDateButtonClick = onOffOnDateButtonClick,
        onOnButtonClick = onOnButtonClick,
      )
    }

    floatingActionButtonSpacerItem()
  }
}

@Composable
private fun rememberMonthlyCalendarState(
  initialState: UpcomingAlarmsCalendarState
): EpicDatePickerState =
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
private fun WeeklyMonthlyCalendar(
  initialState: UpcomingAlarmsCalendarState,
  modifier: Modifier = Modifier,
  alarmCounts: Map<LocalDate, Int> = emptyMap(),
  onSelectedDateChange: (LocalDate?) -> Unit = {},
  onMonthlyDateRangeChange: (ClosedRange<LocalDate>) -> Unit = {},
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

  suspend fun PagerState.scrollToDate(destinationDate: LocalDate) {
    scrollToPage(
      (destinationDate.previousDayOfWeek(firstDayOfWeek()).toEpochDays() -
          today.previousDayOfWeek(firstDayOfWeek()).toEpochDays())
        .coerceAtLeast(0) / 7
    )
  }

  var calendarMode by rememberSaveable { mutableStateOf(CalendarMode.WEEKLY) }
  val monthlyCalendarState = rememberMonthlyCalendarState(initialState)

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
      weeklyCalendarState.scrollToDate(
        monthlyCalendarState.selectedDates.firstOrNull()?.takeIf {
          it.month == monthlyCalendarState.pagerState.currentMonth.month
        } ?: with(monthlyCalendarState.pagerState.currentMonth) { LocalDate(year, month, 1) }
      )
    }

    onMonthlyDateRangeChange(
      monthlyCalendarDateRange(
        month = monthlyCalendarState.pagerState.currentMonth,
        config = monthlyCalendarState.pagerState.config.basisConfig,
      )
    )
  }

  LaunchedEffect(monthlyCalendarState.selectedDates) {
    onSelectedDateChange(monthlyCalendarState.selectedDates.firstOrNull())
  }

  Crossfade(targetState = calendarMode, modifier = modifier) { mode ->
    Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
      monthlyCalendarState.selectedDates.firstOrNull()?.let {
        Text(
          text =
            "${it.dayOfWeek.localized()}, ${it.month.name.take(3).lowercase().capitalize(Locale.current)} ${it.dayOfMonth}",
          style = MaterialTheme.typography.headlineMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(start = 16.dp),
        )
      }

      fun onDayOfMonthClick(date: LocalDate) {
        if (date !in monthlyCalendarState.selectedDates) {
          monthlyCalendarState.toggleDateSelection(date)
        }
      }

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
                  alarmCounts = alarmCounts,
                  onDayOfMonthClick = ::onDayOfMonthClick,
                )
              }
            }
          }

          TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { calendarMode = CalendarMode.MONTHLY },
          ) {
            Text(stringResource(Res.string.expand))
          }
        }
        CalendarMode.MONTHLY -> {
          DatePickerYearMonthControls(
            pagerState = monthlyCalendarState.pagerState,
            modifier = Modifier.fillMaxWidth(),
          )

          CompositionLocalProvider(
            LocalEpicDatePickerConfig provides monthlyCalendarState.config,
            LocalEpicDatePickerState provides monthlyCalendarState,
          ) {
            val selectionMode = monthlyCalendarState.selectionMode
            val selectedDays = monthlyCalendarState.selectedDates
            val ranges =
              remember(selectionMode, selectedDays) {
                when (selectionMode) {
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
                Modifier.drawEpicRanges(
                  ranges = ranges,
                  color = monthlyCalendarState.config.selectionContainerColor,
                )
              },
              state = monthlyCalendarState.pagerState,
              onDayOfMonthClick = ::onDayOfMonthClick,
              dayOfWeekContent = DayOfWeekEllipsizedContent,
              dayOfMonthContent = { date ->
                val basisState = LocalBasisEpicCalendarState.current!!
                val pickerState = LocalEpicDatePickerState.current!!

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  @Composable
                  fun DayText() {
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
                        if (date in pickerState.selectedDates) {
                          pickerState.config.selectionContentColor
                        } else {
                          pickerState.config.pagerConfig.basisConfig.contentColor
                        },
                    )
                  }

                  alarmCounts[date]
                    ?.takeIf { it > 0 }
                    ?.let { BadgedBox(badge = { Badge { Text(it.toString()) } }) { DayText() } }
                    ?: DayText()
                }
              },
            )
          }

          TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { calendarMode = CalendarMode.WEEKLY },
          ) {
            Text(stringResource(Res.string.collapse))
          }
        }
      }
    }
  }
}

private enum class CalendarMode {
  WEEKLY,
  MONTHLY,
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

private fun monthlyCalendarDateRange(
  month: EpicMonth,
  config: BasisEpicCalendarConfig,
): ClosedRange<LocalDate> {
  val firstDayOfWeek = config.daysOfWeek.first()
  val previousMonth = month.previous()
  val nextMonth = month.next()
  val previousMonthLastDayOfWeek = previousMonth.lastDayOfWeek()

  val lastDaysAmountInPreviousMonth =
    when (firstDayOfWeek) {
      DayOfWeek.MONDAY -> previousMonthLastDayOfWeek.isoDayNumber
      DayOfWeek.SUNDAY -> {
        if (previousMonthLastDayOfWeek == DayOfWeek.SATURDAY) 0
        else previousMonthLastDayOfWeek.isoDayNumber + 1
      }
      else -> error("Unexpected firstDayOfWeek: $firstDayOfWeek")
    } % EpicCalendarConstants.DayOfWeekAmount
  val daysAmountInCurrentMonth = month.numberOfDays
  val firstDaysAmountInNextMonth =
    EpicCalendarConstants.GridCellAmount - lastDaysAmountInPreviousMonth - daysAmountInCurrentMonth

  val startDate =
    if (lastDaysAmountInPreviousMonth > 0) {
      previousMonth.atDay(previousMonth.numberOfDays + 1 - lastDaysAmountInPreviousMonth)
    } else {
      month.atDay(1)
    }
  val endDate =
    if (firstDaysAmountInNextMonth > 0) {
      nextMonth.atDay(firstDaysAmountInNextMonth)
    } else {
      month.atDay(daysAmountInCurrentMonth)
    }

  return startDate..endDate
}
