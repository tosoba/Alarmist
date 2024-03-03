package com.trm.alarmist.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnNotificationUseCase
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val updateAlarmOnFiredUseCase: UpdateAlarmOnNotificationUseCase by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    when (intent?.action) {
      ACTION_ALARM_FIRED -> {
        val alarmId = getAlarmId(intent)
        context?.notifyAlarmFired(alarmId.toInt())
        launch { updateAlarmOnFiredUseCase(alarmId, getAlarmFireOnDateTime(intent)) }
      }
      ACTION_ALARM_UPCOMING -> {
        context?.notifyAlarmUpcoming(getAlarmId(intent).toInt(), getAlarmFireOnDateTime(intent))
      }
      else -> {
        return
      }
    }
  }

  private fun getAlarmId(intent: Intent): Long =
    intent.getLongExtra(EXTRA_ALARM_ID, -1).takeIf { it > -1 }
      ?: throw IllegalArgumentException("Missing required EXTRA_ALARM_ID.")

  private fun getAlarmFireOnDateTime(intent: Intent): LocalDateTime =
    LocalDateTime.parse(
      requireNotNull(intent.getStringExtra(EXTRA_FIRE_ON_DATE_TIME)) {
        "Missing required EXTRA_FIRE_ON_DATE."
      }
    )

  companion object {
    private const val ACTION_ALARM_FIRED = "ALARM_FIRED"
    private const val ACTION_ALARM_UPCOMING = "ALARM_UPCOMING"

    private const val EXTRA_ALARM_ID = "ALARM_ID"
    private const val EXTRA_FIRE_ON_DATE_TIME = "FIRE_ON_DATE_TIME"

    fun alarmUpcomingIntent(context: Context, id: Long, fireOnDateTime: LocalDateTime): Intent =
      Intent(context, AlarmBroadcastReceiver::class.java)
        .setAction(ACTION_ALARM_UPCOMING)
        .putExtra(EXTRA_ALARM_ID, id)
        .putExtra(EXTRA_FIRE_ON_DATE_TIME, fireOnDateTime.toString())

    fun alarmFiredIntent(context: Context): Intent =
      Intent(context, AlarmBroadcastReceiver::class.java).setAction(ACTION_ALARM_FIRED)

    fun alarmFiredIntent(context: Context, id: Long, fireOnDateTime: LocalDateTime): Intent =
      alarmFiredIntent(context)
        .putExtra(EXTRA_ALARM_ID, id)
        .putExtra(EXTRA_FIRE_ON_DATE_TIME, fireOnDateTime.toString())
  }
}
