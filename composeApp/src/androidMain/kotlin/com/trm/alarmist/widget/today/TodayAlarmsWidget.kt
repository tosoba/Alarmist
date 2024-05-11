package com.trm.alarmist.widget.today

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledTodayUseCase
import com.trm.alarmist.widget.util.WidgetColumn
import com.trm.alarmist.widget.util.widgetBackgroundCornerRadius
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
      GlanceTheme {
        WidgetColumn(modifier = GlanceModifier.fillMaxSize().widgetBackgroundCornerRadius()) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // TODO: app icon on the left
            Column(modifier = GlanceModifier.defaultWeight()) {
              // TODO: label if exists
              Text("Today") // TODO: larger font
            }
            // TODO: refresh image
          }
          when {
            !alarms.initialized -> {
              // TODO: progress indicator
            }
            alarms.data.isEmpty() -> {
              Row(verticalAlignment = Alignment.CenterVertically) {
                // TODO: no alarms image on the left
                Column(modifier = GlanceModifier.defaultWeight()) {
                  Text("Schedule an alarm") // TODO: larger font
                  Text("No alarms scheduled")
                }
              }
            }
            else -> {
              LazyColumn {
                items(alarms.data, itemId = AlarmListModel::id) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    // TODO: item similar to one in app with fireAtTime/time + label + switch on the
                    // right
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
