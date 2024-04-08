package com.trm.alarmist.core.common.util

import androidx.compose.runtime.Composable
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

@Composable
actual fun is24HoursFormat(): Boolean =
  NSDateFormatter.dateFormatFromTemplate("j", 0u, NSLocale.currentLocale)?.contains("a") == true
