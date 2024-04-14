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
    groupId: Long?,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    snoozeDurationMinutes: Long,
    snoozeLimit: Long
  ): Long

  suspend fun editAlarm(
    id: Long,
    groupId: Long?,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    snoozeDurationMinutes: Long,
    snoozeLimit: Long
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

  suspend fun getOnAlarmsAndResetMissedAlarms(): List<AlarmModel>

  suspend fun toggleAlarmOnOff(id: Long): AlarmModel

  suspend fun toggleAlarmOnOffOnDate(id: Long, date: LocalDate): AlarmModel

  suspend fun updateGroupAlarmsOnOff(groupId: Long, isOn: Boolean): List<AlarmModel>

  suspend fun updateUngroupedAlarmsOnOff(isOn: Boolean): List<AlarmModel>

  suspend fun updateAlarmOnDismiss(id: Long, notificationDateTime: LocalDateTime): AlarmModel

  suspend fun updateAlarmOnSnooze(id: Long): AlarmModel

  suspend fun deleteAlarm(id: Long)

  suspend fun addGroup(name: String, color: Int, alarmIds: Collection<Long>)

  suspend fun editGroup(id: Long, name: String, color: Int, alarmIds: Collection<Long>)

  suspend fun deleteGroup(id: Long)
}
