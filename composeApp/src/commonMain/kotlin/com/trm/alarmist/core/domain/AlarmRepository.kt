package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.model.AlarmScheduleModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
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

  fun getAlarmsInGroupFlow(groupId: Long): Flow<List<AlarmListModel>>

  fun getUngroupedAlarmsFlow(): Flow<List<AlarmListModel>>

  fun getOnAlarmsScheduledToFireOnDate(date: LocalDate): Flow<List<AlarmListModel>>

  fun getOnAlarmSchedulesForDates(dates: ClosedRange<LocalDate>): Flow<List<AlarmScheduleModel>>

  fun getOnOneTimeAlarmsBeforeTime(time: LocalTime): Flow<List<AlarmListModel>>

  fun getOnOneTimeAlarmsAfterTime(time: LocalTime): Flow<List<AlarmListModel>>

  fun countOnOneTimeAlarmsBeforeTime(time: LocalTime): Flow<Int>

  fun countOnOneTimeAlarmsAfterTime(time: LocalTime): Flow<Int>

  suspend fun getAndUpdateOnAlarms(): List<AlarmModel>

  suspend fun toggleAlarmOnOff(id: Long): AlarmModel

  suspend fun updateGroupAlarmsOnOff(groupId: Long, isOn: Boolean): List<AlarmModel>

  suspend fun updateUngroupedAlarmsOnOff(isOn: Boolean): List<AlarmModel>

  suspend fun updateAlarmOnNotification(id: Long, notificationDateTime: LocalDateTime): AlarmModel

  suspend fun addGroup(name: String, color: Int, alarmIds: Collection<Long>)

  suspend fun editGroup(id: Long, name: String, color: Int, alarmIds: Collection<Long>)
}
