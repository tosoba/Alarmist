package com.trm.alarmist.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissedUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnFiredUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val updateAlarmOnFiredUseCase: UpdateAlarmOnFiredUseCase by inject()
  private val updateAlarmOnDismissedUseCase: UpdateAlarmOnDismissedUseCase by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    // TODO: update alarm dismiss notification to alarm fired - check if notify with the same id
    // updates notification
    when (intent?.action) {
      ACTION_ALARM_FIRED -> {
        val alarmId = getAlarmId(intent)
        context?.notifyAlarmFired(alarmId.toInt())
        launch { updateAlarmOnFiredUseCase(alarmId) }
      }
      ACTION_ALARM_UPCOMING -> {
        val alarmId = getAlarmId(intent)
        context?.notifyAlarmUpcoming(alarmId.toInt())
        launch { updateAlarmOnDismissedUseCase(alarmId) }
      }
      else -> {
        return
      }
    }

    // TODO: schedule next alarm if exists from both notification types
    // TODO: turn off alarm scheduled for dates if it was its last scheduled date
  }

  private fun getAlarmId(intent: Intent): Long =
    intent.getLongExtra(EXTRA_ALARM_ID, -1).takeIf { it > -1 }
      ?: throw IllegalArgumentException("Missing required EXTRA_ALARM_ID.")

  companion object {
    private const val ACTION_ALARM_FIRED = "ALARM_FIRED"
    private const val ACTION_ALARM_UPCOMING = "ALARM_UPCOMING"

    private const val EXTRA_ALARM_ID = "ALARM_ID"

    fun alarmUpcomingIntent(context: Context, id: Long): Intent =
      Intent(context, AlarmBroadcastReceiver::class.java)
        .setAction(ACTION_ALARM_UPCOMING)
        .putExtra(EXTRA_ALARM_ID, id)

    fun alarmFiredIntent(context: Context, id: Long): Intent =
      Intent(context, AlarmBroadcastReceiver::class.java)
        .setAction(ACTION_ALARM_FIRED)
        .putExtra(EXTRA_ALARM_ID, id)
  }
}
