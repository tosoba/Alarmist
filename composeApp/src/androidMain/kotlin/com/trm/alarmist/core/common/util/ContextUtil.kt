package com.trm.alarmist.core.common.util

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.widget.today.TodayWidgetPinPreview
import com.trm.alarmist.widget.today.TodayWidgetReceiver

fun Context.getActivity(): Activity? =
  when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
  }

suspend fun Context.pinWidget(
  providerInfo: AppWidgetProviderInfo,
  callback: PendingIntent? = null,
) {
  GlanceAppWidgetManager(this)
    .requestPinGlanceAppWidget(
      getWidgetReceiverClassFor(providerInfo.provider),
      when (providerInfo.provider) {
        widgetReceiverComponentName<TodayWidgetReceiver>() -> TodayWidgetPinPreview()
        else -> null
      },
      null,
      callback,
    )
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
