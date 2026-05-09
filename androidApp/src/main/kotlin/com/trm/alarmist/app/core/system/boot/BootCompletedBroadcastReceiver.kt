package com.trm.alarmist.app.core.system.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.GetAndResetMissedAlarmsOnBootUseCase
import com.trm.alarmist.app.core.system.alarm.notifyAlarmMissed
import com.trm.alarmist.app.core.system.alarm.notifyMultipleAlarmsMissed
import io.github.aakira.napier.Napier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootCompletedBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val getAndResetMissedAlarmsOnBootUseCase: GetAndResetMissedAlarmsOnBootUseCase by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Intent.ACTION_BOOT_COMPLETED && intent?.action != Intent.ACTION_REBOOT) {
      return
    }

    launch {
      val missedAlarms = getAndResetMissedAlarmsOnBootUseCase()
      if (missedAlarms.isEmpty()) {
        Napier.d("No missed alarms.", tag = this::class.simpleName)
        return@launch
      }

      missedAlarms.forEach { (alarm, fireOnDateTimes) ->
        if (fireOnDateTimes.size > 1) {
          context?.notifyMultipleAlarmsMissed(alarm.id.toInt(), fireOnDateTimes, alarm.name)
        } else {
          context?.notifyAlarmMissed(alarm.id.toInt(), fireOnDateTimes.first(), alarm.name)
        }
      }
    }
  }
}
