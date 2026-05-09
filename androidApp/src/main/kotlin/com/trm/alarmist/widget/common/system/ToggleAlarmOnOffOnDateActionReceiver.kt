package com.trm.alarmist.widget.common.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffOnDateUseCase
import com.trm.alarmist.widget.clock.ClockWidgetReceiver
import com.trm.alarmist.widget.common.util.WidgetAction
import com.trm.alarmist.widget.common.util.WidgetExtra
import com.trm.alarmist.widget.common.util.updateAllWidgetsIntent
import com.trm.alarmist.widget.group.GroupWidgetReceiver
import com.trm.alarmist.widget.today.TodayWidgetReceiver
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ToggleAlarmOnOffOnDateActionReceiver : BroadcastReceiver(), KoinComponent {
  private val toggleAlarmOnOffOnDateUseCase: ToggleAlarmOnOffOnDateUseCase by inject()

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != WidgetAction.TOGGLE_ALARM_ON_OFF_ON_DATE) return

    val extras =
      requireNotNull(intent.extras) {
        "Extras were not provided to TOGGLE_ALARM_ON_OFF_ON_DATE action."
      }
    launch {
      toggleAlarmOnOffOnDateUseCase(
        id = extras.getLong(WidgetExtra.ALARM_ID),
        date = LocalDate.fromEpochDays(intent.getIntExtra(WidgetExtra.ALARM_FIRE_DATE, 0)),
      )

      context.sendBroadcast(context.updateAllWidgetsIntent<GroupWidgetReceiver>())
      context.sendBroadcast(context.updateAllWidgetsIntent<ClockWidgetReceiver>())
      context.sendBroadcast(context.updateAllWidgetsIntent<TodayWidgetReceiver>())
    }
  }
}
