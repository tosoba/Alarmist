package com.trm.alarmist.core.domain

import kotlinx.datetime.LocalDateTime

interface AlarmsRepository {
  fun scheduleAlarm(time: LocalDateTime, name: String? = null)
}
