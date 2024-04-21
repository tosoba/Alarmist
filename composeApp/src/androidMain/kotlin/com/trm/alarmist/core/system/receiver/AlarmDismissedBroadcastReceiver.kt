package com.trm.alarmist.core.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.system.AlarmFireSettings
import com.trm.alarmist.core.system.EXTRA_ALARM_FIRE_SETTINGS
import com.trm.alarmist.core.system.cancelNotification
import com.trm.alarmist.core.system.getAlarmFireSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmDismissedBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val updateAlarmOnDismissUseCase: UpdateAlarmOnDismissUseCase by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_DISMISSED) return

    val settings = getAlarmFireSettings(intent)
    launch { updateAlarmOnDismissUseCase(settings.id, settings.fireOnDateTime) }
    context?.cancelNotification(settings.id.toInt())
  }

  companion object {
    private const val ACTION_ALARM_DISMISSED = "ALARM_DISMISSED"

    fun intent(context: Context, settings: AlarmFireSettings): Intent =
      Intent(context, AlarmDismissedBroadcastReceiver::class.java)
        .setAction(ACTION_ALARM_DISMISSED)
        .putExtra(EXTRA_ALARM_FIRE_SETTINGS, settings)
  }
}
