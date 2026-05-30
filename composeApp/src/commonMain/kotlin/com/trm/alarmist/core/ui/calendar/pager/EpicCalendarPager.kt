package com.trm.alarmist.core.ui.calendar.pager

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trm.alarmist.core.ui.calendar.basis.BasisDayOfMonthContent
import com.trm.alarmist.core.ui.calendar.basis.BasisDayOfWeekContent
import com.trm.alarmist.core.ui.calendar.basis.BasisEpicCalendar
import com.trm.alarmist.core.ui.calendar.basis.DefaultDayOfMonthContent
import com.trm.alarmist.core.ui.calendar.basis.DefaultDayOfWeekContent
import com.trm.alarmist.core.ui.calendar.basis.getByIndex
import com.trm.alarmist.core.ui.calendar.basis.state.rememberBasisEpicCalendarState
import com.trm.alarmist.core.ui.calendar.pager.config.LocalEpicCalendarPagerConfig
import com.trm.alarmist.core.ui.calendar.pager.state.EpicCalendarPagerState
import com.trm.alarmist.core.ui.calendar.pager.state.LocalEpicCalendarPagerState
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Composable
fun EpicCalendarPager(
  modifier: Modifier = Modifier,
  pageModifier: (page: Int) -> Modifier = { Modifier },
  state: EpicCalendarPagerState = LocalEpicCalendarPagerState.current,
  onDayOfMonthClick: ((LocalDate) -> Unit)? = null,
  onDayOfWeekClick: ((DayOfWeek) -> Unit)? = null,
  dayOfWeekContent: BasisDayOfWeekContent = DefaultDayOfWeekContent,
  dayOfMonthContent: BasisDayOfMonthContent = DefaultDayOfMonthContent,
) {
  CompositionLocalProvider(
    LocalEpicCalendarPagerConfig provides state.config,
    LocalEpicCalendarPagerState provides state,
  ) {
    HorizontalPager(
      modifier = modifier,
      state = state.pagerState,
      verticalAlignment = Alignment.Top,
    ) { page ->
      BasisEpicCalendar(
        modifier = pageModifier(page),
        state =
          rememberBasisEpicCalendarState(
            currentMonth = remember(state.monthRange, page) { state.monthRange.getByIndex(page) },
            config = state.config.basisConfig,
          ),
        onDayOfMonthClick = onDayOfMonthClick,
        onDayOfWeekClick = onDayOfWeekClick,
        dayOfMonthContent = dayOfMonthContent,
        dayOfWeekContent = dayOfWeekContent,
      )
    }
  }
}
