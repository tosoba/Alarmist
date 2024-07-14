package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import com.trm.alarmist.R

@Composable
fun WidgetAlarmListTextClock(
  widgetLayoutSize: WidgetLayoutSize,
  modifier: GlanceModifier = GlanceModifier,
) {
  val context = LocalContext.current

  Column(modifier = modifier) {
    Box {
      WidgetTextClock(
        format12Hour =
          context.getString(
            if (widgetLayoutSize != WidgetLayoutSize.Small) R.string.time_format_12_h_full
            else R.string.time_format_12_h_short
          ),
        format24Hour =
          context.getString(
            if (widgetLayoutSize != WidgetLayoutSize.Small) R.string.time_format_24_h_full
            else R.string.time_format_24_h_short
          ),
      ) {
        setFloat(
          R.id.widget_text_clock,
          "setTextSize",
          context.resources
            .getInteger(
              if (widgetLayoutSize == WidgetLayoutSize.Large) {
                R.integer.widget_text_clock_large_font_size
              } else {
                R.integer.widget_text_clock_normal_font_size
              }
            )
            .toFloat(),
        )
      }
    }

    Box {
      val amPmFormat =
        context.getString(
          if (widgetLayoutSize != WidgetLayoutSize.Small) R.string.time_format_am_pm_date_full
          else R.string.time_format_am_pm_date_short
        )
      WidgetTextClock(format12Hour = amPmFormat, format24Hour = amPmFormat) {
        setFloat(
          R.id.widget_text_clock,
          "setTextSize",
          context.resources.getInteger(R.integer.widget_text_clock_am_pm_font_size).toFloat(),
        )
      }
    }
  }
}
