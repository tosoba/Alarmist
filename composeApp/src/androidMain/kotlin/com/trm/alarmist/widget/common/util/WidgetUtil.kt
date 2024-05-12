package com.trm.alarmist.widget.common.util

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal fun GlanceAppWidget.updateWidget(
  widgetId: Int,
  context: Context,
  updateState: suspend (MutablePreferences) -> Unit,
) {
  CoroutineScope(context = SupervisorJob() + Dispatchers.Default).launch {
    val glanceId = context.getGlanceIdByWidgetId(widgetId)
    updateAppWidgetState(context = context, glanceId = glanceId, updateState = updateState)
    update(context, glanceId)
  }
}

internal inline fun <reified T : GlanceAppWidget> T.updateAllWidgets(
  context: Context,
  noinline updateState: suspend (MutablePreferences) -> Unit,
) {
  CoroutineScope(context = SupervisorJob() + Dispatchers.Default).launch {
    for (glanceId in context.getGlanceIds<T>()) {
      updateAppWidgetState(context = context, glanceId = glanceId, updateState = updateState)
      update(context, glanceId)
    }
  }
}

private suspend inline fun <reified T : GlanceAppWidget> Context.getGlanceIds(): List<GlanceId> =
  GlanceAppWidgetManager(this).getGlanceIds(T::class.java)

private fun Context.getGlanceIdByWidgetId(widgetId: Int): GlanceId =
  GlanceAppWidgetManager(this).getGlanceIdBy(widgetId)

internal object WidgetAction {
  const val UPDATE_ALL_WIDGETS = "ACTION_UPDATE_ALL_WIDGETS"
  const val UPDATE_WIDGET = "ACTION_UPDATE_WIDGET"
}

internal object WidgetExtra {
  const val WIDGET_ID = "WIDGET_ID"
}

internal inline fun <reified T : GlanceAppWidgetReceiver> Context.updateWidgetIntent(
  widgetId: Int
): Intent = actionIntent<T>(WidgetAction.UPDATE_WIDGET).putExtra(WidgetExtra.WIDGET_ID, widgetId)

internal inline fun <reified T : GlanceAppWidgetReceiver> Context.updateAllWidgetsIntent(): Intent =
  actionIntent<T>(WidgetAction.UPDATE_ALL_WIDGETS)

private inline fun <reified T> Context.actionIntent(action: String): Intent =
  Intent(this, T::class.java).also { it.action = action }

internal val uuidKey: Preferences.Key<String>
  get() = stringPreferencesKey("uuid")

internal fun updateUuid(prefs: MutablePreferences) {
  prefs[uuidKey] = UUID.randomUUID().toString()
}
