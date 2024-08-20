package com.trm.alarmist.core.ui.calendar.pager.state

import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import kotlinx.datetime.Month

fun defaultEpicCalendarPagerMonthRange() =
  EpicMonth(1900, Month.JANUARY)..EpicMonth(2100, Month.DECEMBER)
