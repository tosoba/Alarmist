package com.trm.alarmist.core.ui.calendar.basis.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.trm.alarmist.core.ui.calendar.basis.EpicMonth
import com.trm.alarmist.core.ui.calendar.basis.config.BasisEpicCalendarConfig
import com.trm.alarmist.core.ui.calendar.basis.config.LocalBasisEpicCalendarConfig

@Stable
class ImmutableBasisEpicCalendarState(
  override val currentMonth: EpicMonth,
  override val config: BasisEpicCalendarConfig,
) : BasisEpicCalendarState {
  override val dateGridInfo by derivedStateOf {
    calculateEpicCalendarGridInfo(currentMonth = currentMonth, config = config)
  }
}

@Stable
@Composable
fun rememberBasisEpicCalendarState(
  currentMonth: EpicMonth = LocalBasisEpicCalendarState.current?.currentMonth ?: EpicMonth.now(),
  config: BasisEpicCalendarConfig = LocalBasisEpicCalendarConfig.current,
): BasisEpicCalendarState =
  remember(currentMonth, config) {
    ImmutableBasisEpicCalendarState(currentMonth = currentMonth, config = config)
  }
