package com.trm.alarmist.core.ui.calendar.datepicker.config

import androidx.compose.ui.graphics.Color
import com.trm.alarmist.core.ui.calendar.pager.config.DefaultEpicCalendarPagerConfig

val DefaultEpicDatePickerConfig =
  ImmutableEpicDatePickerConfig(
    pagerConfig = DefaultEpicCalendarPagerConfig,
    selectionContainerColor = Color.Red,
    selectionContentColor = Color.White,
  )
