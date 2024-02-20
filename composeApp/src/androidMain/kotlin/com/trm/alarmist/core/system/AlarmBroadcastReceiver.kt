package com.trm.alarmist.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    // TODO: update alarm dismiss notification to alarm fired - check if notify with the same id
    // updates notification
    when (intent?.action) {
      ACTION_ALARM_FIRED -> {
        context?.notifyAlarmFired(
          intent.getLongExtra(EXTRA_ALARM_ID, -1).takeIf { it > -1 }?.toInt()
            ?: throw IllegalArgumentException("Missing required EXTRA_ALARM_ID.")
        )
      }
      ACTION_ALARM_UPCOMING -> {
        context?.notifyAlarmUpcoming(
          intent.getLongExtra(EXTRA_ALARM_ID, -1).takeIf { it > -1 }?.toInt()
            ?: throw IllegalArgumentException("Missing required EXTRA_ALARM_ID.")
        )
      }
      else -> return
    }

    // TODO: schedule next alarm if exists from both notification types
    // TODO: turn off alarm scheduled for dates if it was its last scheduled date
  }

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
