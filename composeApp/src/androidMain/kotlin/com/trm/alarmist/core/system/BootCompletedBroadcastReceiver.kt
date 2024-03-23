package com.trm.alarmist.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.GetAndResetMissedAlarmsOnBootUseCase
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootCompletedBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val getAndResetMissedAlarmsOnBootUseCase:
    GetAndResetMissedAlarmsOnBootUseCase by
    inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Intent.ACTION_BOOT_COMPLETED && intent?.action != Intent.ACTION_REBOOT) {
      return
    }

    launch {
      val missedAlarms =
        getAndResetMissedAlarmsOnBootUseCase()
      if (missedAlarms.isEmpty()) return@launch

      missedAlarms.forEach {
        Napier.e("${it.key.id} - ${it.value.joinToString(transform = LocalDateTime::toString)}")
      }

      // TODO: create notifications with grouped missed alarms
    }
  }
}
