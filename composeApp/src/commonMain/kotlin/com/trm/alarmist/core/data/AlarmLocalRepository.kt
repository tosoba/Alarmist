package com.trm.alarmist.core.data

import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.datetime.LocalDateTime

internal class AlarmLocalRepository(private val db: AlarmistDatabase) : AlarmRepository {
  override fun scheduleAlarm(time: LocalDateTime, name: String?) {
  }
}
