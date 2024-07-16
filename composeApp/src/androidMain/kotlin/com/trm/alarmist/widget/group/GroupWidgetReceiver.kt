package com.trm.alarmist.widget.group

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.widget.common.util.actionIntent
import com.trm.alarmist.widget.common.util.updateById
import com.trm.alarmist.widget.handleAction

class GroupWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GroupWidget = GroupWidget()

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)

    if (glanceAppWidget.handleAction(context, intent)) return

    if (intent.action == ACTION_UPDATE_GROUP) {
      handleUpdateGroup(intent, context)
    }
  }

  private fun handleUpdateGroup(intent: Intent, context: Context) {
    val extras =
      requireNotNull(intent.extras) { "Extras were not provided to $ACTION_UPDATE_GROUP action." }
    glanceAppWidget.updateById(
      widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID),
      context = context,
      updateState = { prefs -> prefs[groupIdKey] = extras.getLong(EXTRA_GROUP_ID) },
    )
  }

  internal companion object {
    private const val EXTRA_GROUP_ID = "GROUP_ID"
    private const val ACTION_UPDATE_GROUP = "UPDATE_GROUP"

    val groupIdKey: Preferences.Key<Long>
      get() = longPreferencesKey("groupId")

    fun updateGroupIntent(context: Context, widgetId: Int, groupId: Long): Intent =
      context
        .actionIntent<GroupWidgetReceiver>(ACTION_UPDATE_GROUP)
        .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        .putExtra(EXTRA_GROUP_ID, groupId)
  }
}
