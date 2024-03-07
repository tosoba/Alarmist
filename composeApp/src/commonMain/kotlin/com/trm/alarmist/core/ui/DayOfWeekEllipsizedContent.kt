package com.trm.alarmist.core.ui

import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import epicarchitect.calendar.compose.basis.BasisDayOfWeekContent
import epicarchitect.calendar.compose.basis.config.LocalBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.localized

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
