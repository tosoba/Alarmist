package com.trm.alarmist.core.system

import android.content.Context
import com.trm.alarmist.widget.clock.ClockWidgetReceiver
import com.trm.alarmist.widget.common.util.updateAllWidgetsIntent
import com.trm.alarmist.widget.group.GroupWidgetReceiver
import com.trm.alarmist.widget.today.TodayWidgetReceiver

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

  override fun updateTodayWidgets() {
    context.sendBroadcast(context.updateAllWidgetsIntent<TodayWidgetReceiver>())
  }
}
