package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.RowScope
import androidx.glance.layout.padding
import com.trm.alarmist.R

@Composable
fun RowScope.WidgetAlarmListTextClock(widgetLayoutSize: WidgetLayoutSize) {
  val context = LocalContext.current

  Column(
    modifier =
      GlanceModifier.defaultWeight().run {
        if (widgetLayoutSize != WidgetLayoutSize.Large) padding(start = 16.dp) else this
      }
  ) {
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
          context.resources.getInteger(R.integer.widget_text_clock_large_font_size).toFloat(),
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
