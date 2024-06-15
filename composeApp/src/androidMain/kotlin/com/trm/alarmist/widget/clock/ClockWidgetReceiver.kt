package com.trm.alarmist.widget.clock

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.widget.handleAction

class ClockWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: ClockWidget = ClockWidget()

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    glanceAppWidget.handleAction(context, intent)
  }
}
