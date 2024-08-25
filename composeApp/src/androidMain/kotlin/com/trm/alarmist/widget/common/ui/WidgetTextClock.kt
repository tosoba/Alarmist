package com.trm.alarmist.widget.common.ui

import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.trm.alarmist.R

@Composable
fun WidgetTextClock(
  format12Hour: CharSequence,
  format24Hour: CharSequence,
  showShadow: Boolean = false,
  configure: @Composable RemoteViews.() -> Unit = {},
) {
  AndroidRemoteViews(
    remoteViews =
      RemoteViews(
          LocalContext.current.packageName,
          if (showShadow) R.layout.widget_shadow_text_clock else R.layout.widget_text_clock,
        )
        .apply {
          setCharSequence(R.id.widget_text_clock, "setFormat12Hour", format12Hour)
          setCharSequence(R.id.widget_text_clock, "setFormat24Hour", format24Hour)
          configure()
        }
  )
}
