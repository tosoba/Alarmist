package com.trm.alarmist.core.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.domain.model.AlarmFireSettings
import com.trm.alarmist.core.common.util.EXTRA_ALARM_FIRE_SETTINGS
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.common.util.requireAlarmFireSettings
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTime
import com.trm.alarmist.core.system.notifyAlarmUpcoming
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmUpcomingBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val isAlarmScheduledToFireAtDateTime: IsAlarmScheduledToFireAtDateTime by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_UPCOMING) return

    val settings = intent.requireAlarmFireSettings()
    launch {
      if (isAlarmScheduledToFireAtDateTime(settings.id, settings.fireOnDateTime)) {
        context?.notifyAlarmUpcoming(settings)
      }
    }
  }

  companion object {
    private const val ACTION_ALARM_UPCOMING = "ALARM_UPCOMING"

    fun intent(context: Context, settings: AlarmFireSettings): Intent =
      Intent(context, AlarmUpcomingBroadcastReceiver::class.java)
        .setAction(ACTION_ALARM_UPCOMING)
        .putExtra(EXTRA_ALARM_FIRE_SETTINGS, settings)
  }
}
