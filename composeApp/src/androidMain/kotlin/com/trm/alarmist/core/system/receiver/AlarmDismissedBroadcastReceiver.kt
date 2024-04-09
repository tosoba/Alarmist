package com.trm.alarmist.core.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.system.EXTRA_ALARM_ID
import com.trm.alarmist.core.system.EXTRA_FIRE_ON_DATE_TIME
import com.trm.alarmist.core.system.cancelNotification
import com.trm.alarmist.core.system.getAlarmFireOnDateTime
import com.trm.alarmist.core.system.getAlarmId
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmDismissedBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val updateAlarmOnDismissUseCase: UpdateAlarmOnDismissUseCase by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_DISMISSED) return

    val alarmId = getAlarmId(intent)
    launch { updateAlarmOnDismissUseCase(alarmId, getAlarmFireOnDateTime(intent)) }
    context?.cancelNotification(alarmId.toInt())
  }

  companion object {
    private const val ACTION_ALARM_DISMISSED = "ALARM_DISMISSED"

    fun intent(context: Context, id: Long, fireOnDateTime: LocalDateTime): Intent =
      Intent(context, AlarmDismissedBroadcastReceiver::class.java)
        .setAction(ACTION_ALARM_DISMISSED)
        .putExtra(EXTRA_ALARM_ID, id)
        .putExtra(EXTRA_FIRE_ON_DATE_TIME, fireOnDateTime.toString())
  }
}
