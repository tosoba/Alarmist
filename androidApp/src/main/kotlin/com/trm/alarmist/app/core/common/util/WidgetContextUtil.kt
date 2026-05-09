package com.trm.alarmist.app.core.common.util

import android.app.PendingIntent
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.app.widget.clock.ClockWidgetPinPreview
import com.trm.alarmist.app.widget.clock.ClockWidgetReceiver
import com.trm.alarmist.app.widget.group.GroupWidgetPinPreview
import com.trm.alarmist.app.widget.group.GroupWidgetReceiver
import com.trm.alarmist.app.widget.today.TodayWidgetPinPreview
import com.trm.alarmist.app.widget.today.TodayWidgetReceiver

suspend fun Context.pinWidget(
  providerInfo: AppWidgetProviderInfo,
  callback: PendingIntent? = null,
) {
  GlanceAppWidgetManager(this)
    .requestPinGlanceAppWidget(
      getWidgetReceiverClassFor(providerInfo.provider),
      glanceAppWidgetPreview(providerInfo = providerInfo, noLazyLayouts = false),
      null,
      callback,
    )
}

fun Context.glanceAppWidgetPreview(
  providerInfo: AppWidgetProviderInfo,
  noLazyLayouts: Boolean,
): GlanceAppWidget? =
  when (providerInfo.provider) {
    widgetReceiverComponentName<TodayWidgetReceiver>() -> TodayWidgetPinPreview(noLazyLayouts)
    widgetReceiverComponentName<ClockWidgetReceiver>() -> ClockWidgetPinPreview()
    widgetReceiverComponentName<GroupWidgetReceiver>() -> GroupWidgetPinPreview(noLazyLayouts)
    else -> null
  }

internal inline fun <reified T : GlanceAppWidgetReceiver> Context.widgetReceiverComponentName():
  ComponentName = ComponentName(applicationContext.packageName, T::class.java.name)

private fun getWidgetReceiverClassFor(
  componentName: ComponentName
): Class<out GlanceAppWidgetReceiver> {
  return try {
    val widgetReceiverClass = Class.forName(componentName.className)
    if (GlanceAppWidgetReceiver::class.java.isAssignableFrom(widgetReceiverClass)) {
      @Suppress("UNCHECKED_CAST")
      widgetReceiverClass as Class<out GlanceAppWidgetReceiver>
    } else {
      throw ClassCastException("The specified component is not a BroadcastReceiver")
    }
  } catch (ex: ClassNotFoundException) {
    throw RuntimeException("Failed to find class for component: $componentName", ex)
  }
}
