package com.trm.alarmist.core.ui.calendar.pager.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.trm.alarmist.core.ui.calendar.basis.config.BasisEpicCalendarConfig

@Immutable
data class ImmutableEpicCalendarPagerConfig(override val basisConfig: BasisEpicCalendarConfig) :
  EpicCalendarPagerConfig

@Composable
fun rememberEpicCalendarPagerConfig(
  basisConfig: BasisEpicCalendarConfig = LocalEpicCalendarPagerConfig.current.basisConfig
): EpicCalendarPagerConfig = remember(basisConfig) { ImmutableEpicCalendarPagerConfig(basisConfig) }
