package com.trm.alarmist.core.common.util

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle

fun Context.getActivity(): Activity? =
  when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
  }

fun Context.pinWidget(
  providerInfo: AppWidgetProviderInfo,
  extras: Bundle? = null,
  callback: PendingIntent? = null,
) {
  AppWidgetManager.getInstance(this).requestPinAppWidget(providerInfo.provider, extras, callback)
}
