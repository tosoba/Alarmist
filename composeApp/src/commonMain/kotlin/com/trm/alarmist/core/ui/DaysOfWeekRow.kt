package com.trm.alarmist.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.trm.alarmist.core.common.util.now
import epicarchitect.calendar.compose.basis.BasisDayOfMonthContent
import epicarchitect.calendar.compose.basis.config.DefaultBasisEpicCalendarConfig
import epicarchitect.calendar.compose.datepicker.config.LocalEpicDatePickerConfig
import epicarchitect.calendar.compose.datepicker.state.LocalEpicDatePickerState
import kotlinx.datetime.LocalDate

@Composable
fun DaysOfWeekRow(
  rowDates: List<LocalDate>,
  modifier: Modifier = Modifier,
  selectedDates: List<LocalDate> = emptyList(),
  alarmCounts: Map<LocalDate, Int> = emptyMap(),
  onDayOfMonthClick: ((LocalDate) -> Unit)? = null,
  dayOfMonthContent: BasisDayOfMonthContent = { date ->
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      @Composable
      fun DayText() {
        Text(
          modifier = Modifier.alpha(alpha = if (date >= LocalDate.now()) 1.0f else 0.5f),
          text = date.dayOfMonth.toString(),
          textAlign = TextAlign.Center,
          color =
            LocalEpicDatePickerState.current!!.config.run {
              if (date in selectedDates) selectionContentColor
              else pagerConfig.basisConfig.contentColor
            },
        )
      }

      alarmCounts[date]
        ?.takeIf { it > 0 }
        ?.let { BadgedBox(badge = { Badge { Text(it.toString()) } }) { DayText() } } ?: DayText()
    }
  },
) {
  Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
    rowDates.forEach { date ->
      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
        Box(
          modifier =
            Modifier.clip(DefaultBasisEpicCalendarConfig.dayOfMonthShape)
              .height(DefaultBasisEpicCalendarConfig.dayOfMonthViewHeight)
              .width(DefaultBasisEpicCalendarConfig.columnWidth)
              .let {
                if (date in selectedDates) {
                  it.background(LocalEpicDatePickerConfig.current.selectionContainerColor)
                } else {
                  it
                }
              }
              .let {
                if (onDayOfMonthClick == null) it else it.clickable { onDayOfMonthClick(date) }
              },
          contentAlignment = Alignment.Center,
        ) {
          dayOfMonthContent(date)
        }
      }
    }
  }
}
