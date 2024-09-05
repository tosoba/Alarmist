package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import com.trm.alarmist.R
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.integerResource
import com.trm.alarmist.widget.common.util.stringResource

@Composable
fun WidgetAlarmListTextClock(modifier: GlanceModifier = GlanceModifier) {
  val widgetLayoutSize = LocalWidgetLayoutType.current
  Column(modifier = modifier) {
    Box {
      WidgetTextClock(
        format12Hour =
          stringResource(
            if (widgetLayoutSize !is WidgetLayoutType.Small) R.string.time_format_12_h_full
            else R.string.time_format_12_h_short
          ),
        format24Hour =
          stringResource(
            if (widgetLayoutSize !is WidgetLayoutType.Small) R.string.time_format_24_h_full
            else R.string.time_format_24_h_short
          ),
      ) {
        setFloat(
          R.id.widget_text_clock,
          "setTextSize",
          integerResource(
              if (widgetLayoutSize is WidgetLayoutType.Large) {
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
        stringResource(
          if (widgetLayoutSize !is WidgetLayoutType.Small) R.string.time_format_am_pm_date_full
          else R.string.time_format_am_pm_date_short
        )
      WidgetTextClock(format12Hour = amPmFormat, format24Hour = amPmFormat) {
        setFloat(
          R.id.widget_text_clock,
          "setTextSize",
          integerResource(R.integer.widget_text_clock_am_pm_font_size).toFloat(),
        )
      }
    }
  }
}
