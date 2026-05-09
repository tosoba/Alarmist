package com.trm.alarmist.app.widget.common.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import com.trm.alarmist.app.widget.clock.ClockWidgetReceiver
import com.trm.alarmist.app.widget.common.util.WidgetAction
import com.trm.alarmist.app.widget.common.util.WidgetExtra
import com.trm.alarmist.app.widget.common.util.updateAllWidgetsIntent
import com.trm.alarmist.app.widget.group.GroupWidgetReceiver
import com.trm.alarmist.app.widget.today.TodayWidgetReceiver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ToggleAlarmOnOffActionReceiver : BroadcastReceiver(), KoinComponent {
  private val toggleAlarmOnOffUseCase: ToggleAlarmOnOffUseCase by inject()

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != WidgetAction.TOGGLE_ALARM_ON_OFF) return

    val extras =
      requireNotNull(intent.extras) { "Extras were not provided to TOGGLE_ALARM_ON_OFF action." }
    launch {
      toggleAlarmOnOffUseCase(id = extras.getLong(WidgetExtra.ALARM_ID))

      context.sendBroadcast(context.updateAllWidgetsIntent<GroupWidgetReceiver>())
      context.sendBroadcast(context.updateAllWidgetsIntent<ClockWidgetReceiver>())
      context.sendBroadcast(context.updateAllWidgetsIntent<TodayWidgetReceiver>())
    }
  }
}
