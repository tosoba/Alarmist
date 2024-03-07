package com.trm.alarmist.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.trm.alarmist.core.common.util.launch
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootCompletedBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  private val alarmRepository by inject<AlarmRepository>()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Intent.ACTION_BOOT_COMPLETED && intent?.action != Intent.ACTION_REBOOT) {
      return
    }

    launch {
      val now = LocalDateTime.now()
      val alarms = alarmRepository.getAllOnAlarms()
      val (everydayAlarms, customScheduleAlarms) =
        alarms.partition { it.scheduledOnDaysOfWeek.isEmpty() && it.scheduledOnDates.isEmpty() }
      val missedEverydayAlarms =
        everydayAlarms.filter {
          val nextFireOnDateTime = calculateAlarmNextFireOnDateTime(it)
          nextFireOnDateTime != null &&
            nextFireOnDateTime.date.minus(1, DateTimeUnit.DAY) != it.lastNotificationDate
        }
      // TODO: consider adding createdOnDate to alarm to calculate number of missed alarms

      // TODO: calculate missedCustomScheduleAlarms - take offOnDates into account
    }
  }
}
