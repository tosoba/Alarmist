package com.trm.alarmist.widget.next

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.widget.handleAction

class NextAlarmWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: NextAlarmWidget = NextAlarmWidget()

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    glanceAppWidget.handleAction(context, intent)
  }
}
