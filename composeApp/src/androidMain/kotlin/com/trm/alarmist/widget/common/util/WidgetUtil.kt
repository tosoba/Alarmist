package com.trm.alarmist.widget.common.util

import android.appwidget.AppWidgetManager
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
import com.trm.alarmist.widget.common.ToggleAlarmOnOffActionReceiver
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal fun GlanceAppWidget.updateById(
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

internal inline fun <reified T : GlanceAppWidget> T.updateAll(
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
  const val TOGGLE_ALARM_ON_OFF = "TOGGLE_ALARM_ON_OFF"
}

internal object WidgetExtra {
  const val ALARM_ID = "ALARM_ID"
  const val ALARM_FIRE_DATE = "ALARM_FIRE_DATE"
}

internal inline fun <reified T : GlanceAppWidgetReceiver> Context.updateWidgetIntent(
  widgetId: Int
): Intent =
  actionIntent<T>(WidgetAction.UPDATE_WIDGET)
    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

internal fun Context.toggleAlarmOnOffIntent(alarmId: Long, alarmFireDate: LocalDate): Intent =
  actionIntent<ToggleAlarmOnOffActionReceiver>(WidgetAction.TOGGLE_ALARM_ON_OFF)
    .putExtra(WidgetExtra.ALARM_ID, alarmId)
    .putExtra(WidgetExtra.ALARM_FIRE_DATE, alarmFireDate.toEpochDays())

internal inline fun <reified T : GlanceAppWidgetReceiver> Context.updateAllWidgetsIntent(): Intent =
  actionIntent<T>(WidgetAction.UPDATE_ALL_WIDGETS)

internal inline fun <reified T> Context.actionIntent(action: String): Intent =
  Intent(this, T::class.java).also { it.action = action }

internal val uuidKey: Preferences.Key<String>
  get() = stringPreferencesKey("uuid")

internal fun updateUuid(prefs: MutablePreferences) {
  prefs[uuidKey] = UUID.randomUUID().toString()
}
