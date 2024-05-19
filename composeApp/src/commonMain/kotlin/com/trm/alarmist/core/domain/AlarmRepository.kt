package com.trm.alarmist.core.domain

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.model.AlarmScheduleModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
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
    snoozeLimit: Long,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
    soundId: String?,
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
    snoozeLimit: Long,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
    soundId: String?,
  )

  suspend fun getAlarmById(id: Long): AlarmModel

  fun getAllAlarmsListFlow(): Flow<List<AlarmListModel>>

  suspend fun getAllOnAlarmsList(): List<AlarmListModel>

  fun getAllAlarmGroupsFlow(): Flow<List<AlarmGroupModel>>

  fun getAlarmsInGroupFlow(groupId: Long): Flow<List<AlarmListModel>>

  fun getUngroupedAlarmsFlow(): Flow<List<AlarmListModel>>

  fun getAlarmsScheduledToFireOnDateFlow(date: LocalDate): Flow<List<UpcomingAlarmListModel>>

  fun getAlarmsScheduledToFireOnDateAfterTimeFlow(
    date: LocalDate,
    time: LocalTime,
  ): Flow<List<UpcomingAlarmListModel>>

  suspend fun getOnAlarmsScheduledToFireOnDateAfterTime(
    date: LocalDate,
    time: LocalTime,
  ): List<AlarmListModel>

  fun getOnAlarmSchedulesForDatesFlow(dates: ClosedRange<LocalDate>): Flow<List<AlarmScheduleModel>>

  fun getOneTimeAlarmsBeforeTimeFlow(time: LocalTime): Flow<List<UpcomingAlarmListModel>>

  fun getOneTimeAlarmsAfterTimeFlow(time: LocalTime): Flow<List<UpcomingAlarmListModel>>

  suspend fun getOnOneTimeAlarmsAfterTime(time: LocalTime): List<AlarmListModel>

  fun countOnOneTimeAlarmsBeforeTimeFlow(time: LocalTime): Flow<Int>

  fun countOnOneTimeAlarmsAfterTimeFlow(time: LocalTime): Flow<Int>

  suspend fun getOnAlarmsAndResetMissedAlarms(): List<AlarmModel>

  suspend fun toggleAlarmOnOff(id: Long): AlarmModel

  suspend fun toggleAlarmOnOffOnDate(id: Long, date: LocalDate): AlarmModel

  suspend fun turnAlarmOnOnDate(id: Long, date: LocalDate): AlarmModel

  suspend fun turnAlarmOffOnDate(id: Long, date: LocalDate): AlarmModel

  suspend fun updateGroupAlarmsOnOff(groupId: Long, isOn: Boolean): List<AlarmModel>

  suspend fun updateUngroupedAlarmsOnOff(isOn: Boolean): List<AlarmModel>

  suspend fun updateAlarmOnDismiss(id: Long, notificationDateTime: LocalDateTime): AlarmModel

  suspend fun updateAlarmOnSnooze(id: Long): AlarmModel

  suspend fun deleteAlarm(id: Long)

  suspend fun addGroup(name: String, color: Int, alarmIds: Collection<Long>)

  suspend fun editGroup(id: Long, name: String, color: Int, alarmIds: Collection<Long>)

  suspend fun deleteGroup(id: Long)
}
