package com.trm.alarmist.app.core.system

import com.trm.alarmist.core.system.WidgetManager
import android.content.Context
import com.trm.alarmist.app.widget.clock.ClockWidgetReceiver
import com.trm.alarmist.app.widget.common.util.updateAllWidgetsIntent
import com.trm.alarmist.app.widget.group.GroupWidgetReceiver
import com.trm.alarmist.app.widget.today.TodayWidgetReceiver

class AndroidWidgetManager(private val context: Context) : WidgetManager {
  override fun updateAllWidgets() {
    context.sendBroadcast(context.updateAllWidgetsIntent<GroupWidgetReceiver>())
    context.sendBroadcast(context.updateAllWidgetsIntent<ClockWidgetReceiver>())
    context.sendBroadcast(context.updateAllWidgetsIntent<TodayWidgetReceiver>())
  }

  override fun updateWidgetGroup(widgetId: Int, groupId: Long) {
    context.sendBroadcast(
      GroupWidgetReceiver.updateGroupIntent(
        context = context,
        widgetId = widgetId,
        groupId = groupId,
      )
    )
  }

  override fun updateDateSensitiveWidgets() {
    context.sendBroadcast(context.updateAllWidgetsIntent<TodayWidgetReceiver>())
    context.sendBroadcast(context.updateAllWidgetsIntent<ClockWidgetReceiver>())
  }
}
