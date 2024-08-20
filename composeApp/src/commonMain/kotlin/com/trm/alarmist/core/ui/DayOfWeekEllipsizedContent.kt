package com.trm.alarmist.core.ui

import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.trm.alarmist.core.ui.calendar.basis.BasisDayOfWeekContent
import com.trm.alarmist.core.ui.calendar.basis.config.LocalBasisEpicCalendarConfig
import com.trm.alarmist.core.ui.calendar.basis.localized

val DayOfWeekEllipsizedContent: BasisDayOfWeekContent = { dayOfWeek ->
  val config = LocalBasisEpicCalendarConfig.current
  Text(
    text = dayOfWeek.localized(),
    textAlign = TextAlign.Center,
    color = config.contentColor,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
  )
}
