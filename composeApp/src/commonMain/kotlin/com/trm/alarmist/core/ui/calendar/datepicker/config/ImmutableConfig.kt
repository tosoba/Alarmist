package com.trm.alarmist.core.ui.calendar.datepicker.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.trm.alarmist.core.ui.calendar.pager.config.EpicCalendarPagerConfig

@Immutable
data class ImmutableEpicDatePickerConfig(
  override val pagerConfig: EpicCalendarPagerConfig,
  override val selectionContentColor: Color,
  override val selectionContainerColor: Color,
) : EpicDatePickerConfig

@Composable
fun rememberEpicDatePickerConfig(
  pagerConfig: EpicCalendarPagerConfig = LocalEpicDatePickerConfig.current.pagerConfig,
  selectionContentColor: Color = LocalEpicDatePickerConfig.current.selectionContentColor,
  selectionContainerColor: Color = LocalEpicDatePickerConfig.current.selectionContainerColor,
): EpicDatePickerConfig =
  remember(pagerConfig, selectionContentColor, selectionContainerColor) {
    ImmutableEpicDatePickerConfig(
      pagerConfig = pagerConfig,
      selectionContentColor = selectionContentColor,
      selectionContainerColor = selectionContainerColor,
    )
  }
