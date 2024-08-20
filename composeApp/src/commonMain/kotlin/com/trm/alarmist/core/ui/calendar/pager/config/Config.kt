package com.trm.alarmist.core.ui.calendar.pager.config

import androidx.compose.runtime.compositionLocalOf
import com.trm.alarmist.core.ui.calendar.basis.config.BasisEpicCalendarConfig

interface EpicCalendarPagerConfig {
  val basisConfig: BasisEpicCalendarConfig
}

val LocalEpicCalendarPagerConfig =
  compositionLocalOf<EpicCalendarPagerConfig> { DefaultEpicCalendarPagerConfig }
