package com.trm.alarmist.core.domain

import com.trm.alarmist.db.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalTime

interface AlarmRepository {
  suspend fun addOneShotAlarm(fireAt: LocalTime, name: String? = null)

  fun getAllAlarms(): Flow<List<Alarm>>
}
