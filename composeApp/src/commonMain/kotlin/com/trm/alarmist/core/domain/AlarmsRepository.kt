package com.trm.alarmist.core.domain

import kotlinx.datetime.LocalTime

interface AlarmsRepository {
  suspend fun addOneShotAlarm(fireAt: LocalTime, name: String? = null)
}
