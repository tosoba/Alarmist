package com.trm.alarmist.widget.group

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.ui.WidgetLayoutType
import com.trm.alarmist.widget.common.util.AppWidgetIdProvider
import com.trm.alarmist.widget.common.util.LocalAppWidgetIdProvider
import com.trm.alarmist.widget.common.util.LocalIsPreview
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GroupWidget : GlanceAppWidget(), KoinComponent {
  private val repository: AlarmRepository by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val widgetState by
        produceState<GroupWidgetState>(GroupWidgetState.Uninitialized, state) {
          value =
            state[GroupWidgetReceiver.groupIdKey]?.let {
              GroupWidgetState.Initialized(
                alarms = repository.getAlarmsInGroup(it).map(::WidgetAlarmListModel),
                group = repository.getGroupById(it),
              )
            } ?: GroupWidgetState.NoGroupSet
        }
      val widgetManager = remember(context) { GlanceAppWidgetManager(context) }

      CompositionLocalProvider(
        LocalIsPreview provides false,
        LocalWidgetLayoutType provides WidgetLayoutType.fromWidgetSize(),
        LocalAppWidgetIdProvider provides AppWidgetIdProvider(widgetManager::getAppWidgetId),
      ) {
        GroupWidgetScaffold(id = id, state = widgetState)
      }
    }
  }
}
