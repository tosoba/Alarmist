package com.trm.alarmist.core.common.util

import android.content.Context
import android.text.format.DateFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

fun LocalDateTime.formatted(context: Context): String = buildString {
  append(
    date.format(
      LocalDate.Format {
        year(padding = Padding.ZERO)
        char('-')
        monthNumber(padding = Padding.ZERO)
        char('-')
        day(padding = Padding.ZERO)
      }
    )
  )

  append(", ")

  append(
    time.format(
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
  )
}
