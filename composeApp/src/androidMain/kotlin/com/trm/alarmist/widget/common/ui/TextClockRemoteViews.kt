package com.trm.alarmist.widget.common.ui

import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.trm.alarmist.R

@Composable
fun TextClockRemoteViews(useFullTimeFormat: Boolean, configure: RemoteViews.() -> Unit = {}) {
  val context = LocalContext.current
  AndroidRemoteViews(
    remoteViews =
      RemoteViews(context.packageName, R.layout.widget_text_clock_remote_view).apply {
        setCharSequence(
          R.id.location_clock,
          "setFormat12Hour",
          context.getString(
            if (useFullTimeFormat) R.string.time_format_12_h_full
            else R.string.time_format_12_h_short
          ),
        )
        setCharSequence(
          R.id.location_clock,
          "setFormat24Hour",
          context.getString(
            if (useFullTimeFormat) R.string.time_format_24_h_full
            else R.string.time_format_24_h_short
          ),
        )
        configure()
      }
  )
}
