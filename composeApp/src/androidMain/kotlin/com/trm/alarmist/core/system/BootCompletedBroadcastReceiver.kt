package com.trm.alarmist.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.usecase.CalculateMissedAlarmsDateTimesUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootCompletedBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val calculateMissedAlarmsDateTimesUseCase: CalculateMissedAlarmsDateTimesUseCase by
    inject()
  private val alarmRepository: AlarmRepository by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Intent.ACTION_BOOT_COMPLETED && intent?.action != Intent.ACTION_REBOOT) {
      return
    }

    launch {
      calculateMissedAlarmsDateTimesUseCase()
      // TODO: create notifications with grouped missed alarms

      // >>TODO<<: consider performing all operations in a single transactions:
      // - returning missed alarms
      // - reset past scheduled on days only
      // - update all on modification dates to now to prevent showing missed alarms multiple times
      // after multiple reboots

      // >>TODO<<: consider replacing SQLDelight with noSQL solution to make operations on dates
      // such as calculating missed alarms (and other things) easier
    }
  }
}
