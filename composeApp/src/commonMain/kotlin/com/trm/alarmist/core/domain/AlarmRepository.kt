package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.model.AlarmGroupModel
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
  ): Long

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

  fun getAllAlarmGroupsFlow(): Flow<List<AlarmGroupModel>>

  suspend fun toggleAlarmOnOff(id: Long): AlarmModel

  suspend fun updateAlarmOnFired(id: Long): AlarmModel

  suspend fun updateAlarmOnDismissed(id: Long): AlarmModel
}
