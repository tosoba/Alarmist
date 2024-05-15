package com.trm.alarmist.widget.today

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledTodayUseCase
import com.trm.alarmist.widget.common.ui.WidgetActionButtonContent
import com.trm.alarmist.widget.common.ui.WidgetAlarmListItem
import com.trm.alarmist.widget.common.ui.WidgetHeader
import com.trm.alarmist.widget.common.ui.WidgetLoadingIndicator
import com.trm.alarmist.widget.common.ui.WidgetOuterColumn
import com.trm.alarmist.widget.common.util.LocalIsPreviewProvider
import com.trm.alarmist.widget.common.util.smallFontSize
import com.trm.alarmist.widget.common.util.turnAlarmOffIntent
import com.trm.alarmist.widget.common.util.updateWidgetIntent
import com.trm.alarmist.widget.common.util.widgetBackgroundCornerRadius
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayAlarmsWidget : GlanceAppWidget(), KoinComponent {
  private val getAlarmsScheduledTodayUseCase: GetAlarmsScheduledTodayUseCase by inject()

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val state = currentState<Preferences>()
      val alarms by
        produceState(Initializable(emptyList()), state) {
          value = Initializable(getAlarmsScheduledTodayUseCase(), true)
        }
      CompositionLocalProvider(LocalIsPreviewProvider provides false) {
        TodayAlarmsWidgetContent(id = id, alarms = alarms)
      }
    }
  }
}

@Composable
private fun TodayAlarmsWidgetContent(id: GlanceId, alarms: Initializable<List<AlarmListModel>>) {
  GlanceTheme {
    WidgetOuterColumn(modifier = GlanceModifier.fillMaxSize().widgetBackgroundCornerRadius()) {
      val context = LocalContext.current
      val widgetManager = remember(id) { GlanceAppWidgetManager(context) }

      WidgetHeader(
        text = "Today",
        onRefreshClick =
          actionSendBroadcast(
            context.updateWidgetIntent<TodayAlarmsWidgetReceiver>(widgetManager.getAppWidgetId(id))
          ),
        modifier = GlanceModifier.fillMaxWidth(),
      ) {
        Text(
          text = LocalDate.now().toString(),
          maxLines = 1,
          style = TextDefaults.defaultTextStyle.copy(fontSize = smallFontSize.sp),
        )
      }

      when {
        !alarms.initialized -> {
          WidgetLoadingIndicator(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight().padding(vertical = 20.dp)
          )
        }
        alarms.data.isEmpty() -> {
          WidgetActionButtonContent(
            infoText = "No scheduled alarms",
            buttonText = "Schedule an alarm",
            modifier = GlanceModifier.fillMaxWidth().defaultWeight().padding(vertical = 20.dp),
          )
        }
        else -> {
          LazyColumn(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight().padding(vertical = 10.dp)
          ) {
            items(alarms.data, itemId = AlarmListModel::id) {
              WidgetAlarmListItem(
                alarm = it,
                modifier = GlanceModifier.fillMaxWidth(),
                onTurnAlarmOff = actionSendBroadcast(context.turnAlarmOffIntent(it.id)),
              )
            }
          }
        }
      }
    }
  }
}
