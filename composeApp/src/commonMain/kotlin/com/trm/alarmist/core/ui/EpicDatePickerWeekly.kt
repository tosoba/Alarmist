package com.trm.alarmist.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import epicarchitect.calendar.compose.basis.BasisDayOfMonthContent
import epicarchitect.calendar.compose.basis.BasisDayOfWeekContent
import epicarchitect.calendar.compose.basis.DefaultDayOfWeekContent
import epicarchitect.calendar.compose.basis.EpicMonth
import epicarchitect.calendar.compose.basis.addMonths
import epicarchitect.calendar.compose.basis.addYears
import epicarchitect.calendar.compose.basis.config.BasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.config.LocalBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.epicMonth
import epicarchitect.calendar.compose.basis.getByIndex
import epicarchitect.calendar.compose.basis.indexOf
import epicarchitect.calendar.compose.basis.size
import epicarchitect.calendar.compose.basis.state.BasisEpicCalendarState
import epicarchitect.calendar.compose.basis.state.ImmutableBasisEpicCalendarState
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.basis.state.rememberBasisEpicCalendarState
import epicarchitect.calendar.compose.datepicker.DefaultDayOfMonthContent
import epicarchitect.calendar.compose.datepicker.config.EpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.config.LocalEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.DefaultEpicDatePickerState
import epicarchitect.calendar.compose.datepicker.state.EpicDatePickerState
import epicarchitect.calendar.compose.pager.config.EpicCalendarPagerConfig
import epicarchitect.calendar.compose.pager.config.LocalEpicCalendarPagerConfig
import epicarchitect.calendar.compose.pager.state.EpicCalendarPagerState
import epicarchitect.calendar.compose.pager.state.defaultEpicCalendarPagerMonthRange
import epicarchitect.calendar.compose.ranges.drawEpicRanges
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

interface EpicCalendarWeeklyPagerState : EpicCalendarPagerState {
  val currentWeekIndex: Int // from 0 to 5 (both inclusive)
}

val LocalEpicCalendarWeeklyPagerState = compositionLocalOf<EpicCalendarWeeklyPagerState?> { null }

interface EpicDatePickerWeeklyState : EpicDatePickerState {
  override val pagerState: EpicCalendarWeeklyPagerState
}

val LocalEpicDatePickerWeeklyState = compositionLocalOf<EpicDatePickerWeeklyState?> { null }

@Stable
class DefaultEpicDatePickerWeeklyState(
  private val wrapped: EpicDatePickerState,
  override val pagerState: EpicCalendarWeeklyPagerState,
) : EpicDatePickerWeeklyState {
  override val config: EpicDatePickerConfig = wrapped.config
  override val selectedDates: List<LocalDate> = wrapped.selectedDates
  override var selectionMode: EpicDatePickerState.SelectionMode = wrapped.selectionMode

  override fun toggleDateSelection(date: LocalDate) {
    wrapped.toggleDateSelection(date)
  }
}

@OptIn(ExperimentalFoundationApi::class)
class DefaultEpicCalendarWeeklyPagerState(
  config: EpicCalendarPagerConfig,
  monthRange: ClosedRange<EpicMonth>,
  override val pagerState: PagerState,
) : EpicCalendarWeeklyPagerState {
  override var config by mutableStateOf(config)
  override var monthRange by mutableStateOf(monthRange)
  override val currentWeekIndex: Int
    get() = pagerState.currentPage

  override val currentMonth
    get() = monthRange.getByIndex(pagerState.currentPage / 6)

  override suspend fun scrollToMonth(month: EpicMonth) {
    monthRange.indexOf(month)?.let { pagerState.animateScrollToPage(it) }
  }

  override suspend fun scrollYears(amount: Int) = scrollToMonth(currentMonth.addYears(amount))

  override suspend fun scrollMonths(amount: Int) = scrollToMonth(currentMonth.addMonths(amount))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberEpicCalendarWeeklyPagerState(
  config: EpicCalendarPagerConfig = LocalEpicCalendarPagerConfig.current,
  monthRange: ClosedRange<EpicMonth> = defaultEpicCalendarPagerMonthRange(),
  initialMonth: EpicMonth = EpicMonth.now(),
  initialWeek: Int = 0, // TODO:
): DefaultEpicCalendarWeeklyPagerState {
  val pagerState =
    rememberPagerState(
      initialPage = remember(monthRange, initialMonth) { monthRange.indexOf(initialMonth) ?: 0 },
      initialPageOffsetFraction = 0f,
      pageCount = { monthRange.size() * 6 },
    )
  return remember(config, monthRange, initialMonth, pagerState) {
    DefaultEpicCalendarWeeklyPagerState(
      config = config,
      pagerState = pagerState,
      monthRange = monthRange,
    )
  }
}

@Stable
@Composable
fun rememberEpicDatePickerWeeklyState(
  config: EpicDatePickerConfig = LocalEpicDatePickerConfig.current,
  monthRange: ClosedRange<EpicMonth> = defaultEpicCalendarPagerMonthRange(),
  initialMonth: EpicMonth = EpicMonth.now(),
  selectedDates: List<LocalDate> = emptyList(),
  selectionMode: EpicDatePickerState.SelectionMode = EpicDatePickerState.SelectionMode.Single(),
): EpicDatePickerWeeklyState {
  val pagerState =
    rememberEpicCalendarWeeklyPagerState(
      config = config.pagerConfig,
      monthRange = monthRange,
      initialMonth = initialMonth,
    )

  return remember(config, selectedDates, selectionMode, pagerState) {
    DefaultEpicDatePickerWeeklyState(
      DefaultEpicDatePickerState(
        config = config,
        selectedDates = selectedDates,
        selectionMode = selectionMode,
        pagerState = pagerState,
      ),
      pagerState = pagerState,
    )
  }
}


// TODO:
//@Stable
//@Composable
//fun rememberBasisEpicCalendarWeeklyState(
//  currentMonth: EpicMonth = LocalBasisEpicCalendarState.current?.currentMonth ?: EpicMonth.now(),
//  config: BasisEpicCalendarConfig = LocalBasisEpicCalendarConfig.current
//): BasisEpicCalendarState = remember(
//  currentMonth,
//  config
//) {
//  ImmutableBasisEpicCalendarState(
//    currentMonth = currentMonth,
//    config = config
//  )
//}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EpicDatePickerWeekly(
  modifier: Modifier = Modifier,
  state: EpicDatePickerWeeklyState =
    LocalEpicDatePickerWeeklyState.current ?: rememberEpicDatePickerWeeklyState(),
  dayOfWeekContent: BasisDayOfWeekContent = DefaultDayOfWeekContent,
  dayOfMonthContent: BasisDayOfMonthContent = DefaultDayOfMonthContent,
) =
  with(state.config) {
    CompositionLocalProvider(
      LocalEpicDatePickerConfig provides state.config,
      LocalEpicDatePickerWeeklyState provides state,
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

      val pagerModifier: (page: Int) -> Modifier = { _ ->
        Modifier.drawEpicRanges(ranges = ranges, color = selectionContainerColor)
      }

      HorizontalPager(
        modifier = modifier,
        state = state.pagerState.pagerState,
        verticalAlignment = Alignment.Top,
      ) { page ->
        BasisEpicCalendarWeekly(
          modifier = pagerModifier(page),
          state =
            rememberBasisEpicCalendarState(
              currentMonth =
                remember(state.pagerState.monthRange, page) {
                  state.pagerState.monthRange.getByIndex(page)
                },
              config = state.pagerState.config.basisConfig,
            ),
          dayOfMonthContent = dayOfMonthContent,
          dayOfWeekContent = dayOfWeekContent,
        )
      }
    }
  }

@Composable
fun BasisEpicCalendarWeekly(
  modifier: Modifier = Modifier,
  state: BasisEpicCalendarState =
    LocalBasisEpicCalendarState.current ?: rememberBasisEpicCalendarState(),
  onDayOfMonthClick: ((LocalDate) -> Unit)? = null,
  onDayOfWeekClick: ((DayOfWeek) -> Unit)? = null,
  dayOfWeekContent: BasisDayOfWeekContent = DefaultDayOfWeekContent,
  dayOfMonthContent: BasisDayOfMonthContent =
    epicarchitect.calendar.compose.basis.DefaultDayOfMonthContent,
) =
  with(state.config) {
    CompositionLocalProvider(
      LocalBasisEpicCalendarConfig provides state.config,
      LocalBasisEpicCalendarState provides state,
    ) {
      Column(
        modifier = modifier.then(Modifier.padding(contentPadding)),
        verticalArrangement = Arrangement.spacedBy(rowsSpacerHeight),
      ) {
        if (displayDaysOfWeek) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            daysOfWeek.forEach { dayOfWeek ->
              Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Box(
                  modifier =
                    Modifier.clip(dayOfWeekShape)
                      .height(dayOfWeekViewHeight)
                      .width(columnWidth)
                      .let {
                        if (onDayOfWeekClick == null) it
                        else it.clickable { onDayOfWeekClick(dayOfWeek) }
                      },
                  contentAlignment = Alignment.Center,
                ) {
                  dayOfWeekContent(dayOfWeek)
                }
              }
            }
          }
        }

        state.dateGridInfo.dateMatrix.forEach { rowDates ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            rowDates.forEach { date ->
              Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (displayDaysOfAdjacentMonths || date.epicMonth == state.currentMonth) {
                  Box(
                    modifier =
                      Modifier.clip(dayOfMonthShape)
                        .height(dayOfMonthViewHeight)
                        .width(columnWidth)
                        .let {
                          if (onDayOfMonthClick == null) it
                          else it.clickable { onDayOfMonthClick(date) }
                        },
                    contentAlignment = Alignment.Center,
                  ) {
                    dayOfMonthContent(date)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
