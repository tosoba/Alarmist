package com.trm.alarmist.core.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTime
import com.trm.alarmist.core.system.AndroidAlarmService
import com.trm.alarmist.core.system.EXTRA_ALARM_ID
import com.trm.alarmist.core.system.EXTRA_FIRE_ON_DATE_TIME
import com.trm.alarmist.core.system.EXTRA_RING_DURATION_MINUTES
import com.trm.alarmist.core.system.EXTRA_SNOOZE_AVAILABLE
import com.trm.alarmist.core.system.EXTRA_SOUND_ENABLED
import com.trm.alarmist.core.system.EXTRA_VIBRATION_ENABLED
import com.trm.alarmist.core.system.getAlarmFireOnDateTime
import com.trm.alarmist.core.system.getAlarmId
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmFiredBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val isAlarmScheduledToFireAtDateTime: IsAlarmScheduledToFireAtDateTime by inject()

  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_FIRED) return

    launch {
      if (
        isAlarmScheduledToFireAtDateTime(
          id = getAlarmId(intent),
          fireAtDateTime = getAlarmFireOnDateTime(intent),
        )
      ) {
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

    fun intent(
      context: Context,
      id: Long,
      fireOnDateTime: LocalDateTime,
      snoozeAvailable: Boolean,
      ringDurationMinutes: Long,
      soundEnabled: Boolean,
      vibrationEnabled: Boolean,
    ): Intent =
      intent(context)
        .putExtra(EXTRA_ALARM_ID, id)
        .putExtra(EXTRA_FIRE_ON_DATE_TIME, fireOnDateTime.toString())
        .putExtra(EXTRA_SNOOZE_AVAILABLE, snoozeAvailable)
        .putExtra(EXTRA_RING_DURATION_MINUTES, ringDurationMinutes)
        .putExtra(EXTRA_SOUND_ENABLED, soundEnabled)
        .putExtra(EXTRA_VIBRATION_ENABLED, vibrationEnabled)
  }
}
