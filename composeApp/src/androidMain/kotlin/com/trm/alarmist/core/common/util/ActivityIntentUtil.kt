package com.trm.alarmist.core.common.util

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent

fun createMainActivityIntent(context: Context): Intent {
  return Intent().setClassName(context.packageName, "com.trm.alarmist.MainActivity")
}

fun createAlarmFiredActivityIntent(context: Context): Intent {
  return Intent().setClassName(context.packageName, "com.trm.alarmist.AlarmFiredActivity")
}

fun createGroupWidgetPinIntent(context: Context, widgetId: Int): Intent {
  return Intent()
    .setClassName(context.packageName, "com.trm.alarmist.widget.group.GroupWidgetConfigActivity")
    .putExtra("IS_PINNED", true)
    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
}

fun createGroupWidgetActionIntent(context: Context, widgetId: Int): Intent {
  return Intent()
    .setClassName(context.packageName, "com.trm.alarmist.widget.group.GroupWidgetConfigActivity")
    .putExtra("IS_PINNED", true)
    .putExtra("WIDGET_ACTION", true)
    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
}
