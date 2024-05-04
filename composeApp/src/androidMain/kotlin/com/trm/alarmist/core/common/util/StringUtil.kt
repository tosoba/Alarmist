package com.trm.alarmist.core.common.util

import android.content.Context
import android.text.format.DateFormat
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

fun LocalDateTime.formattedTime(context: Context): String =
  "${dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${time.format(
      LocalTime.Format {
          if (!DateFormat.is24HourFormat(context)) {
              amPmHour(padding = Padding.ZERO)
          } else {
              hour(padding = Padding.ZERO)
          }
          char(':')
          minute(padding = Padding.ZERO)
          
          if (!DateFormat.is24HourFormat(context)) {
              char(' ')
              amPmMarker("AM", "PM")
          }
      }
  )
  }"
