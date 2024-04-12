package com.trm.alarmist.core.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTime
import com.trm.alarmist.core.system.EXTRA_ALARM_ID
import com.trm.alarmist.core.system.EXTRA_FIRE_ON_DATE_TIME
import com.trm.alarmist.core.system.getAlarmFireOnDateTime
import com.trm.alarmist.core.system.getAlarmId
import com.trm.alarmist.core.system.notifyAlarmUpcoming
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmUpcomingBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val isAlarmScheduledToFireAtDateTime: IsAlarmScheduledToFireAtDateTime by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_UPCOMING) return

    launch {
      if (
        isAlarmScheduledToFireAtDateTime(
          id = getAlarmId(intent),
          fireAtDateTime = getAlarmFireOnDateTime(intent),
        )
      ) {
        context?.notifyAlarmUpcoming(
          id = getAlarmId(intent),
          fireOnDateTime = getAlarmFireOnDateTime(intent),
        )
      }
    }
  }

  companion object {
    private const val ACTION_ALARM_UPCOMING = "ALARM_UPCOMING"

    fun intent(context: Context, id: Long, fireOnDateTime: LocalDateTime): Intent =
      Intent(context, AlarmUpcomingBroadcastReceiver::class.java)
        .setAction(ACTION_ALARM_UPCOMING)
        .putExtra(EXTRA_ALARM_ID, id)
        .putExtra(EXTRA_FIRE_ON_DATE_TIME, fireOnDateTime.toString())
  }
}
