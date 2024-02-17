package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.model.AlarmListItem
import com.trm.alarmist.db.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface AlarmRepository {
  suspend fun addAlarm(
    fireAt: LocalTime,
    name: String? = null,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  )

  fun getAllAlarms(): Flow<List<AlarmListItem>>

  suspend fun toggleAlarmOnOff(id: Long)
}
