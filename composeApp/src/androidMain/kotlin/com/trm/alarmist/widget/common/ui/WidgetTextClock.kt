package com.trm.alarmist.widget.common.ui

import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.trm.alarmist.R

@Composable
internal fun WidgetTextClock(
  format12Hour: CharSequence,
  format24Hour: CharSequence,
  shadowMode: WidgetTextClockShadowMode = WidgetTextClockShadowMode.None,
  configure: @Composable RemoteViews.() -> Unit = {},
) {
  AndroidRemoteViews(
    remoteViews =
      RemoteViews(
          LocalContext.current.packageName,
          when (shadowMode) {
            WidgetTextClockShadowMode.Dark -> R.layout.widget_dark_shadow_text_clock
            WidgetTextClockShadowMode.Light -> R.layout.widget_light_shadow_text_clock
            WidgetTextClockShadowMode.None -> R.layout.widget_text_clock
          },
        )
        .apply {
          setCharSequence(R.id.widget_text_clock, "setFormat12Hour", format12Hour)
          setCharSequence(R.id.widget_text_clock, "setFormat24Hour", format24Hour)
          configure()
        }
  )
}

internal enum class WidgetTextClockShadowMode {
  Dark,
  Light,
  None,
}
