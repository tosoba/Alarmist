package com.trm.alarmist.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.ui.calendar.basis.BasisDayOfMonthContent
import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import com.trm.alarmist.core.ui.calendar.basis.config.DefaultBasisEpicCalendarConfig
import com.trm.alarmist.core.ui.calendar.basis.contains
import com.trm.alarmist.core.ui.calendar.datepicker.config.LocalEpicDatePickerConfig
import com.trm.alarmist.core.ui.calendar.datepicker.state.LocalEpicDatePickerState
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
          modifier =
            Modifier.alpha(
              alpha =
                when {
                  date < LocalDate.now() -> 0.5f
                  date in EpicMonth.now() -> 1.0f
                  else -> 0.5f
                }
            ),
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
        ?.let {
          BadgedBox(
            badge = {
              Badge(modifier = Modifier.offset(y = (-4).dp, x = 4.dp)) { Text(it.toString()) }
            }
          ) {
            DayText()
          }
        } ?: DayText()
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
