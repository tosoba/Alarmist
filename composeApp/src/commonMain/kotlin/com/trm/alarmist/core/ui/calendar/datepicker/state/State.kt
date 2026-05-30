package com.trm.alarmist.core.ui.calendar.datepicker.state

import androidx.compose.runtime.compositionLocalOf

val LocalEpicDatePickerState =
  compositionLocalOf<DefaultEpicDatePickerState> { error("No date picker state provided.") }
