package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface AlarmRepository {
  suspend fun addAlarm(
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  )

  suspend fun editAlarm(
    id: Long,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  )

  suspend fun getAlarmById(id: Long): AlarmModel

  fun getAllAlarmsListFlow(): Flow<List<AlarmListModel>>

  suspend fun toggleAlarmOnOff(id: Long)
}
