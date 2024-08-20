package com.trm.alarmist.core.ui.calendar.datepicker.config

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.trm.alarmist.core.ui.calendar.pager.config.EpicCalendarPagerConfig

interface EpicDatePickerConfig {
  val pagerConfig: EpicCalendarPagerConfig
  val selectionContentColor: Color
  val selectionContainerColor: Color
}

val LocalEpicDatePickerConfig =
  compositionLocalOf<EpicDatePickerConfig> { DefaultEpicDatePickerConfig }
