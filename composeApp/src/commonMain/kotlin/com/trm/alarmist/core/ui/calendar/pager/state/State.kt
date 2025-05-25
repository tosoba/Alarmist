package com.trm.alarmist.core.ui.calendar.pager.state

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.compositionLocalOf
import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import com.trm.alarmist.core.ui.calendar.pager.config.EpicCalendarPagerConfig

interface EpicCalendarPagerState {
  val config: EpicCalendarPagerConfig
  val currentMonth: EpicMonth
  var monthRange: ClosedRange<EpicMonth>
  val pagerState: PagerState

  suspend fun scrollToMonth(month: EpicMonth)

  suspend fun scrollYears(amount: Int)

  suspend fun scrollMonths(amount: Int)
}

val LocalEpicCalendarPagerState = compositionLocalOf<EpicCalendarPagerState?> { null }
