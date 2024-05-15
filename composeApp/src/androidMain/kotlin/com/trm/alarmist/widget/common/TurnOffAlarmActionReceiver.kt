package com.trm.alarmist.widget.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.usecase.ToggleUpcomingAlarmOnOffOnDateUseCase
import com.trm.alarmist.widget.common.util.WidgetAction
import com.trm.alarmist.widget.common.util.WidgetExtra
import com.trm.alarmist.widget.common.util.updateAllWidgetsIntent
import com.trm.alarmist.widget.group.AlarmGroupWidgetReceiver
import com.trm.alarmist.widget.next.NextAlarmWidgetReceiver
import com.trm.alarmist.widget.today.TodayAlarmsWidgetReceiver
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TurnOffAlarmActionReceiver : BroadcastReceiver(), KoinComponent {
  private val toggleUpcomingAlarmOnOffOnDateUseCase: ToggleUpcomingAlarmOnOffOnDateUseCase by
    inject()

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != WidgetAction.TURN_ALARM_OFF) return

    launch {
      toggleUpcomingAlarmOnOffOnDateUseCase(
        id =
          requireNotNull(intent.extras) { "Extras were not provided to TURN_ALARM_OFF action." }
            .getLong(WidgetExtra.ALARM_ID),
        date = LocalDate.now(), //TODO: figure out date for scheduled alarms
      )

      context.sendBroadcast(context.updateAllWidgetsIntent<AlarmGroupWidgetReceiver>())
      context.sendBroadcast(context.updateAllWidgetsIntent<NextAlarmWidgetReceiver>())
      context.sendBroadcast(context.updateAllWidgetsIntent<TodayAlarmsWidgetReceiver>())
    }
  }
}
