package com.trm.alarmist.core.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnNotificationUseCase
import com.trm.alarmist.core.system.EXTRA_ALARM_ID
import com.trm.alarmist.core.system.EXTRA_FIRE_ON_DATE_TIME
import com.trm.alarmist.core.system.getAlarmFireOnDateTime
import com.trm.alarmist.core.system.getAlarmId
import com.trm.alarmist.core.system.notifyAlarmFired
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmFiredBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val updateAlarmOnNotificationUseCase: UpdateAlarmOnNotificationUseCase by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_FIRED) return

    val alarmId = getAlarmId(intent)
    context?.notifyAlarmFired(alarmId)
    launch { updateAlarmOnNotificationUseCase(alarmId, getAlarmFireOnDateTime(intent)) }
  }

  companion object {
    private const val ACTION_ALARM_FIRED = "ALARM_FIRED"

    fun intent(context: Context): Intent =
      Intent(context, AlarmFiredBroadcastReceiver::class.java).setAction(ACTION_ALARM_FIRED)

    fun intent(context: Context, id: Long, fireOnDateTime: LocalDateTime): Intent =
      intent(context)
        .putExtra(EXTRA_ALARM_ID, id)
        .putExtra(EXTRA_FIRE_ON_DATE_TIME, fireOnDateTime.toString())
  }
}
