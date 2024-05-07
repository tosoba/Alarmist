package com.trm.alarmist.widget.today

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledTodayUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayAlarmsWidget : GlanceAppWidget(), KoinComponent {
  private val getAlarmsScheduledTodayUseCase: GetAlarmsScheduledTodayUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val alarms by
        produceState(Initializable(emptyList())) {
          value = Initializable(getAlarmsScheduledTodayUseCase(), true)
        }

      // TODO: scrollable column? Might be problematic in glance...
    }
  }
}
