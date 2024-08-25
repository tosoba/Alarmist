package com.trm.alarmist.widget.today

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.model.Initialized
import com.trm.alarmist.core.common.model.Uninitialized
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.usecase.GetTodayAlarmsUseCase
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayWidget : GlanceAppWidget(), KoinComponent {
  private val getTodayAlarmsUseCase: GetTodayAlarmsUseCase by inject()
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
                alarms = getTodayAlarmsUseCase().map(::WidgetAlarmListModel),
                groups = repository.getAllAlarmGroups().associateBy(AlarmGroupModel::id),
              )
            )
        }

      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        TodayWidgetScaffold(id = id, state = widgetState)
      }
    }
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun TodayWidgetPreview() {
  CompositionLocalProvider(LocalIsPreviewProvider provides true) {
    TodayWidgetScaffold(
      id = object : GlanceId {},
      state = Initialized(TodayWidgetState(alarms = emptyList(), groups = emptyMap())),
    )
  }
}
