package com.trm.alarmist.core.domain

import kotlinx.datetime.LocalDateTime

interface AlarmRepository {
  fun scheduleAlarm(time: LocalDateTime, name: String? = null)
}
