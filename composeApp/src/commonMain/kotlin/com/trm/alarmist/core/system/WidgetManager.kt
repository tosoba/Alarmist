package com.trm.alarmist.core.system

interface WidgetManager {
  fun updateAllWidgets()

  fun updateWidgetGroup(widgetId: Int, groupId: Long)

  fun updateTodayWidgets()
}
