package com.trm.alarmist.widget.today

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.usecase.ToggleUpcomingAlarmOnOffOnDateUseCase
import com.trm.alarmist.widget.common.util.WidgetAction
import com.trm.alarmist.widget.common.util.WidgetExtra
import com.trm.alarmist.widget.common.util.updateAllWidgets
import com.trm.alarmist.widget.common.util.updateUuid
import com.trm.alarmist.widget.common.util.updateWidget
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayAlarmsWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {
  private val toggleUpcomingAlarmOnOffOnDateUseCase: ToggleUpcomingAlarmOnOffOnDateUseCase by
    inject()

  override val glanceAppWidget: TodayAlarmsWidget = TodayAlarmsWidget()

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    when (intent.action) {
      WidgetAction.UPDATE_ALL_WIDGETS -> {
        glanceAppWidget.updateAllWidgets(context, ::updateUuid)
      }
      WidgetAction.UPDATE_WIDGET -> {
        glanceAppWidget.updateWidget(
          widgetId =
            requireNotNull(intent.extras) { "Extras were not provided to UPDATE_WIDGET action." }
              .getInt(WidgetExtra.WIDGET_ID),
          context = context,
          updateState = ::updateUuid,
        )
      }
      WidgetAction.TURN_ALARM_OFF -> {
        launch {
          toggleUpcomingAlarmOnOffOnDateUseCase(
            id =
              requireNotNull(intent.extras) { "Extras were not provided to TURN_ALARM_OFF action." }
                .getLong(WidgetExtra.ALARM_ID),
            date = LocalDate.now(),
          )
          glanceAppWidget.updateAllWidgets(context, ::updateUuid)
          // TODO: add update actions for other types of widgets as well
        }
      }
    }
  }
}
