package com.trm.alarmist.widget.common.ui

import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.now
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun WidgetChronometerCountdown(dateTimeTo: LocalDateTime, configure: RemoteViews.() -> Unit = {}) {
  AndroidRemoteViews(
    remoteViews =
      RemoteViews(LocalContext.current.packageName, R.layout.widget_chronometer).apply {
        setChronometer(
          R.id.widget_chronometer,
          dateTimeTo.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds() -
            now().toEpochMilliseconds(),
          null,
          true,
        )
        setChronometerCountDown(R.id.widget_chronometer, true)
        configure()
      }
  )
}
