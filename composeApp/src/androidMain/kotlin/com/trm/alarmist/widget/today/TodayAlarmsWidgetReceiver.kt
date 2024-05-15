package com.trm.alarmist.widget.today

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.widget.handleAction

class TodayAlarmsWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: TodayAlarmsWidget = TodayAlarmsWidget()

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    handleAction(context, intent)
  }
}
