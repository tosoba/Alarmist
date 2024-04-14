package com.trm.alarmist.core.system

import kotlinx.datetime.LocalDateTime

interface AlarmScheduler {
  fun scheduleAlarm(id: Long, fireOnDateTime: LocalDateTime, snoozeAvailable: Boolean)

  fun cancelAlarm(id: Long)
}
