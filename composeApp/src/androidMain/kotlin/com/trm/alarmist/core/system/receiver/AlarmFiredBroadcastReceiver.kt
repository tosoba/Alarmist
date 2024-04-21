package com.trm.alarmist.core.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTime
import com.trm.alarmist.core.system.AlarmFireSettings
import com.trm.alarmist.core.system.AndroidAlarmService
import com.trm.alarmist.core.system.EXTRA_ALARM_FIRE_SETTINGS
import com.trm.alarmist.core.system.getAlarmFireSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmFiredBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val isAlarmScheduledToFireAtDateTime: IsAlarmScheduledToFireAtDateTime by inject()

  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_FIRED) return

    val settings = getAlarmFireSettings(intent)
    launch {
      if (isAlarmScheduledToFireAtDateTime(settings.id, settings.fireOnDateTime)) {
        ContextCompat.startForegroundService(
          context,
          Intent(context, AndroidAlarmService::class.java).putExtras(intent),
        )
      }
    }
  }

  companion object {
    private const val ACTION_ALARM_FIRED = "ALARM_FIRED"

    fun intent(context: Context): Intent =
      Intent(context, AlarmFiredBroadcastReceiver::class.java).setAction(ACTION_ALARM_FIRED)

    fun intent(context: Context, settings: AlarmFireSettings): Intent =
      intent(context).putExtra(EXTRA_ALARM_FIRE_SETTINGS, settings)
  }
}
