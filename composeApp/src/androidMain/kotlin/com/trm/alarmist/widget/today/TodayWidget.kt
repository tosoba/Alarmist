package com.trm.alarmist.widget.today

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
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.model.Initialized
import com.trm.alarmist.core.common.model.Uninitialized
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.usecase.GetTodayWidgetAlarmsUseCase
import com.trm.alarmist.widget.common.ui.WidgetLayoutType
import com.trm.alarmist.widget.common.ui.WidgetTheme
import com.trm.alarmist.widget.common.util.AppWidgetIdProvider
import com.trm.alarmist.widget.common.util.LocalAppWidgetIdProvider
import com.trm.alarmist.widget.common.util.LocalIsPreview
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayWidget : GlanceAppWidget(), KoinComponent {
  private val getTodayWidgetAlarmsUseCase: GetTodayWidgetAlarmsUseCase by inject()
  private val repository: AlarmRepository by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val widgetState by
        produceState<Initializable<TodayWidgetState>>(Uninitialized, state) {
          value =
            Initialized(
              TodayWidgetState(
                alarms = getTodayWidgetAlarmsUseCase(),
                groups = repository.getAllAlarmGroups().associateBy(AlarmGroupModel::id),
              )
            )
        }
      val widgetManager = remember(context) { GlanceAppWidgetManager(context) }

      CompositionLocalProvider(
        LocalIsPreview provides false,
        LocalWidgetLayoutType provides WidgetLayoutType.fromWidgetSize(),
        LocalAppWidgetIdProvider provides AppWidgetIdProvider(widgetManager::getAppWidgetId),
      ) {
        WidgetTheme { TodayWidgetScaffold(id = id, state = widgetState) }
      }
    }
  }
}
