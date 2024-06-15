package com.trm.alarmist.core.system

import android.content.Context
import com.trm.alarmist.widget.clock.ClockWidgetReceiver
import com.trm.alarmist.widget.common.util.updateAllWidgetsIntent
import com.trm.alarmist.widget.group.AlarmGroupWidgetReceiver
import com.trm.alarmist.widget.today.TodayAlarmsWidgetReceiver

class AndroidWidgetManager(private val context: Context) : WidgetManager {
  override fun updateAllWidgets() {
    context.sendBroadcast(context.updateAllWidgetsIntent<AlarmGroupWidgetReceiver>())
    context.sendBroadcast(context.updateAllWidgetsIntent<ClockWidgetReceiver>())
    context.sendBroadcast(context.updateAllWidgetsIntent<TodayAlarmsWidgetReceiver>())
  }
}
