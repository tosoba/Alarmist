package com.trm.alarmist.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.GetAndUpdateOnAlarmsWithMissedTimestampsOnBootUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootCompletedBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val getAndUpdateOnAlarmsWithMissedTimestampsOnBootUseCase:
    GetAndUpdateOnAlarmsWithMissedTimestampsOnBootUseCase by
    inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Intent.ACTION_BOOT_COMPLETED && intent?.action != Intent.ACTION_REBOOT) {
      return
    }

    launch {
      getAndUpdateOnAlarmsWithMissedTimestampsOnBootUseCase()
      // TODO: create notifications with grouped missed alarms
    }
  }
}
