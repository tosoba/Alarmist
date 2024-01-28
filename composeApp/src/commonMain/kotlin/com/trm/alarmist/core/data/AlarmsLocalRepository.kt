package com.trm.alarmist.core.data

import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.domain.AlarmsRepository
import kotlinx.datetime.LocalDateTime

class AlarmsLocalRepository(private val db: AlarmistDatabase) : AlarmsRepository {
  override fun scheduleAlarm(time: LocalDateTime, name: String?) {
  }
}
