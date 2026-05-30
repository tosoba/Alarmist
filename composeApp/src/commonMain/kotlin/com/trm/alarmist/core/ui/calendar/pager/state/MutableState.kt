package com.trm.alarmist.core.ui.calendar.pager.state

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import com.trm.alarmist.core.ui.calendar.basis.addMonths
import com.trm.alarmist.core.ui.calendar.basis.addYears
import com.trm.alarmist.core.ui.calendar.basis.getByIndex
import com.trm.alarmist.core.ui.calendar.basis.indexOf
import com.trm.alarmist.core.ui.calendar.basis.size
import com.trm.alarmist.core.ui.calendar.pager.config.EpicCalendarPagerConfig
import com.trm.alarmist.core.ui.calendar.pager.config.LocalEpicCalendarPagerConfig

class DefaultEpicCalendarPagerState(
  config: EpicCalendarPagerConfig,
  monthRange: ClosedRange<EpicMonth>,
  override val pagerState: PagerState,
) : EpicCalendarPagerState {
  override var config by mutableStateOf(config)
  override var monthRange by mutableStateOf(monthRange)
  override val currentMonth
    get() = monthRange.getByIndex(pagerState.currentPage)

  override suspend fun scrollToMonth(month: EpicMonth) {
    monthRange.indexOf(month)?.let { pagerState.animateScrollToPage(it) }
  }

  override suspend fun scrollYears(amount: Int) = scrollToMonth(currentMonth.addYears(amount))

  override suspend fun scrollMonths(amount: Int) = scrollToMonth(currentMonth.addMonths(amount))
}

@Composable
fun rememberEpicCalendarPagerState(
  config: EpicCalendarPagerConfig = LocalEpicCalendarPagerConfig.current,
  monthRange: ClosedRange<EpicMonth> = defaultEpicCalendarPagerMonthRange(),
  initialMonth: EpicMonth = EpicMonth.now(),
): DefaultEpicCalendarPagerState {
  val pagerState =
    rememberPagerState(
      initialPage = remember(monthRange, initialMonth) { monthRange.indexOf(initialMonth) ?: 0 },
      initialPageOffsetFraction = 0f,
      pageCount = monthRange::size,
    )
  return remember(config, monthRange, initialMonth, pagerState) {
    DefaultEpicCalendarPagerState(config = config, pagerState = pagerState, monthRange = monthRange)
  }
}
