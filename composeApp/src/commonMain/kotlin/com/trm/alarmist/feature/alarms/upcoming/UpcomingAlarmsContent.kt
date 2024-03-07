package com.trm.alarmist.feature.alarms.upcoming

import DaysOfWeekRow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trm.alarmist.core.common.util.nextDayOfWeek
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.previousDayOfWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpcomingAlarmsContent(modifier: Modifier = Modifier, component: UpcomingAlarmsComponent) {
  val today = LocalDate.now()
  Column(modifier = modifier) {
    HorizontalPager(
      state =
        rememberPagerState {
          weeklyCalendarPagesCount(startDate = today, endDate = LocalDate(2100, Month.DECEMBER, 31))
        },
      modifier = Modifier.fillMaxWidth(),
    ) {
      val startDate = today.previousDayOfWeek(DayOfWeek.SUNDAY).plus(it * 7, DateTimeUnit.DAY)
      val endDate = startDate.plus(6, DateTimeUnit.DAY)
      Column(modifier = Modifier.fillMaxWidth()) {
        DaysOfWeekRow(modifier = Modifier.fillMaxWidth())
        Text("$it week: $startDate - $endDate")
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
