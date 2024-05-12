package com.trm.alarmist.widget.today

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.widget.common.util.WidgetAction
import com.trm.alarmist.widget.common.util.WidgetExtra
import com.trm.alarmist.widget.common.util.updateAllWidgets
import com.trm.alarmist.widget.common.util.updateUuid
import com.trm.alarmist.widget.common.util.updateWidget

class TodayAlarmsWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = TodayAlarmsWidget()

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    when (intent.action) {
      WidgetAction.UPDATE_ALL_WIDGETS -> {
        glanceAppWidget.updateAllWidgets(context, ::updateUuid)
      }
      WidgetAction.UPDATE_WIDGET -> {
        glanceAppWidget.updateWidget(
          widgetId =
            requireNotNull(intent.extras) { "Extras were not provided to UPDATE_WIDGET action." }
              .getInt(WidgetExtra.WIDGET_ID),
          context = context,
          updateState = ::updateUuid,
        )
      }
    }
  }
}
