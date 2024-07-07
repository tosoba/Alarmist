package com.trm.alarmist.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import com.trm.alarmist.widget.common.util.WidgetAction
import com.trm.alarmist.widget.common.util.updateAll
import com.trm.alarmist.widget.common.util.updateById
import com.trm.alarmist.widget.common.util.updateUuid

internal inline fun <reified T : GlanceAppWidget> T.handleAction(
  context: Context,
  intent: Intent,
): Boolean =
  when (intent.action) {
    WidgetAction.UPDATE_ALL_WIDGETS -> {
      updateAll(context, ::updateUuid)
      true
    }
    WidgetAction.UPDATE_WIDGET -> {
      updateById(
        widgetId =
          requireNotNull(intent.extras) { "Extras were not provided to UPDATE_WIDGET action." }
            .getInt(AppWidgetManager.EXTRA_APPWIDGET_ID),
        context = context,
        updateState = ::updateUuid,
      )
      true
    }
    else -> {
      false
    }
  }
