package com.trm.alarmist.core.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.system.EXTRA_ALARM_ID
import com.trm.alarmist.core.system.EXTRA_FIRE_ON_DATE_TIME
import com.trm.alarmist.core.system.getAlarmFireOnDateTime
import com.trm.alarmist.core.system.getAlarmId
import com.trm.alarmist.core.system.notifyAlarmUpcoming
import kotlinx.datetime.LocalDateTime

class AlarmUpcomingBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != ACTION_ALARM_UPCOMING) return

    context?.notifyAlarmUpcoming(getAlarmId(intent), getAlarmFireOnDateTime(intent))
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
