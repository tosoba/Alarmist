package com.trm.alarmist.widget.today

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.widget.handleAction

class TodayWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: TodayWidget = TodayWidget()

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    glanceAppWidget.handleAction(context, intent)
  }
}
