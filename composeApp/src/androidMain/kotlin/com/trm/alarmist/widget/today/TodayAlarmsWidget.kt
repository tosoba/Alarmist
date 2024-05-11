package com.trm.alarmist.widget.today

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledTodayUseCase
import com.trm.alarmist.widget.common.WidgetActionButtonContent
import com.trm.alarmist.widget.common.WidgetAlarmListItem
import com.trm.alarmist.widget.common.WidgetHeader
import com.trm.alarmist.widget.common.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.WidgetOuterColumn
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.widgetBackgroundCornerRadius
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayAlarmsWidget : GlanceAppWidget(), KoinComponent {
  private val getAlarmsScheduledTodayUseCase: GetAlarmsScheduledTodayUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val alarms by
        produceState(Initializable(emptyList())) {
          value = Initializable(getAlarmsScheduledTodayUseCase(), true)
        }
      CompositionLocalProvider(LocalIsPreviewProvider provides true) {
        GlanceTheme {
          WidgetOuterColumn(
            modifier = GlanceModifier.fillMaxSize().widgetBackgroundCornerRadius()
          ) {
            WidgetHeader(modifier = GlanceModifier.fillMaxWidth())

            when {
              !alarms.initialized -> {
                WidgetLoadingIndicator(
                  modifier = GlanceModifier.defaultWeight().padding(vertical = 20.dp)
                )
              }
              alarms.data.isEmpty() -> {
                WidgetActionButtonContent(
                  infoText = "No scheduled alarms",
                  buttonText = "Schedule an alarm",
                  modifier = GlanceModifier.fillMaxSize().padding(vertical = 20.dp),
                )
              }
              else -> {
                LazyColumn(modifier = GlanceModifier.defaultWeight().padding(vertical = 10.dp)) {
                  items(alarms.data, itemId = AlarmListModel::id) { WidgetAlarmListItem(it) }
                }
              }
            }
          }
        }
      }
    }
  }
}
