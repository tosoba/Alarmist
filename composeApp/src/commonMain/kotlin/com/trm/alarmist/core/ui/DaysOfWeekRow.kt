package com.trm.alarmist.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import epicarchitect.calendar.compose.basis.config.LocalBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.contains
import kotlinx.datetime.LocalDate

@Composable
fun DaysOfWeekRow(
  rowDates: List<LocalDate>,
  modifier: Modifier = Modifier,
  onDayOfMonthClick: ((LocalDate) -> Unit)? = null,
  dayOfMonthContent: BasisDayOfMonthContent = {
    Text(
      modifier = Modifier.alpha(alpha = if (it >= LocalDate.now()) 1.0f else 0.5f),
      text = it.dayOfMonth.toString(),
      textAlign = TextAlign.Center,
      color = LocalBasisEpicCalendarConfig.current.contentColor,
    )
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
