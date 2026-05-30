package com.trm.alarmist.core.ui.calendar.basis.state

import androidx.compose.runtime.compositionLocalOf
import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import com.trm.alarmist.core.ui.calendar.basis.config.BasisEpicCalendarConfig

interface BasisEpicCalendarState {
  val config: BasisEpicCalendarConfig
  val currentMonth: EpicMonth
  val dateGridInfo: EpicCalendarGridInfo
}

val LocalBasisEpicCalendarState =
  compositionLocalOf<BasisEpicCalendarState> { error("No calendar state provided.") }
