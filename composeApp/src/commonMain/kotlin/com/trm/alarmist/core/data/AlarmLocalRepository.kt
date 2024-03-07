package com.trm.alarmist.core.data

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.trm.alarmist.core.common.util.DB_OFF
import com.trm.alarmist.core.common.util.DB_ON
import com.trm.alarmist.core.common.util.toListModel
import com.trm.alarmist.core.common.util.toModel
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.AlarmistQueries
import com.trm.alarmist.db.SelectAllGroups
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

class AlarmLocalRepository(
  private val queries: AlarmistQueries,
  private val dispatcher: CoroutineDispatcher,
) : AlarmRepository {
  override suspend fun addAlarm(
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ): Long =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.insertAlarm(
          id = null,
          groupId = null,
          fireAtTime = fireAtTime,
          name = name,
          isOn = if (isOn) 1L else 0L,
          scheduledOnDaysOfWeek = daysOfWeekToDbString(scheduledOnDaysOfWeek),
          scheduledOnDates = datesToDbString(scheduledOnDates),
          offOnDates = datesToDbString(offOnDates),
          lastNotificationDate = null,
        )
        queries.selectLastInsertedRowId().executeAsOne()
      }
    }

  override suspend fun editAlarm(
    id: Long,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    withContext(dispatcher) {
      queries.updateAlarmById(
        id = id,
        groupId = null,
        fireAtTime = fireAtTime,
        name = name,
        isOn = if (isOn) 1L else 0L,
        scheduledOnDaysOfWeek = daysOfWeekToDbString(scheduledOnDaysOfWeek),
        scheduledOnDates = datesToDbString(scheduledOnDates),
        offOnDates = datesToDbString(offOnDates),
      )
    }
  }

  override fun getAllAlarmsListFlow(): Flow<List<AlarmListModel>> =
    queries.selectAllAlarms().asAlarmsListFlow()

  override fun getAllAlarmGroupsFlow(): Flow<List<AlarmGroupModel>> =
    queries.selectAllGroups().asFlow().mapToList(dispatcher).map {
      it.map(SelectAllGroups::toModel)
    }

  override fun getAlarmsInGroupFlow(groupId: Long): Flow<List<AlarmListModel>> =
    queries.selectAlarmsByGroupId(groupId).asAlarmsListFlow()

  override fun getUngroupedAlarmsFlow(): Flow<List<AlarmListModel>> =
    queries.selectUngroupedAlarms().asAlarmsListFlow()

  override suspend fun getAllOnAlarms(): List<AlarmModel> =
    withContext(dispatcher) { queries.selectOnAlarms().executeAsList().map(Alarm::toModel) }

  private fun Query<Alarm>.asAlarmsListFlow(): Flow<List<AlarmListModel>> =
    asFlow().mapToList(dispatcher).map { it.map(Alarm::toListModel) }

  override suspend fun toggleAlarmOnOff(id: Long): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateToggleAlarmOnOffById(id)
        queries.selectAlarmById(id).executeAsOne().toModel()
      }
    }

  override suspend fun updateGroupAlarmsOnOff(groupId: Long, isOn: Boolean): List<AlarmModel> =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateAlarmsOnOffByGroupId(isOn = if (isOn) DB_ON else DB_OFF, groupId = groupId)
        queries.selectAlarmsByGroupId(groupId).executeAsList().map(Alarm::toModel)
      }
    }

  override suspend fun updateUngroupedAlarmsOnOff(isOn: Boolean): List<AlarmModel> =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateUngroupedAlarmsOnOff(isOn = if (isOn) DB_ON else DB_OFF)
        queries.selectUngroupedAlarms().executeAsList().map(Alarm::toModel)
      }
    }

  override suspend fun getAlarmById(id: Long): AlarmModel =
    withContext(dispatcher) { queries.selectAlarmById(id).executeAsOne().toModel() }

  override suspend fun updateAlarmOnNotification(
    id: Long,
    notificationDateTime: LocalDateTime,
  ): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateAlarmLastNotificationDateById(notificationDateTime.date, id)
        val alarm = queries.selectAlarmById(id).executeAsOne().toModel()
        if (
          alarm.scheduledOnDaysOfWeek.isEmpty() &&
            alarm.scheduledOnDates.lastOrNull() == notificationDateTime.date
        ) {
          queries.updateResetAlarmById(id)
          queries.selectAlarmById(id).executeAsOne().toModel()
        } else {
          alarm
        }
      }
    }

  override suspend fun addGroup(name: String, color: Int, alarmIds: Collection<Long>) {
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.insertGroup(id = null, name = name, color = color.toLong())
        if (alarmIds.isNotEmpty()) {
          val groupId = queries.selectLastInsertedRowId().executeAsOne()
          queries.updateAlarmsGroups(groupId = groupId, alarmIds = alarmIds)
        }
      }
    }
  }

  override suspend fun editGroup(id: Long, name: String, color: Int, alarmIds: Collection<Long>) {
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateGroupById(name = name, color = color.toLong(), id = id)
        if (alarmIds.isNotEmpty()) {
          queries.updateAlarmsGroups(groupId = id, alarmIds = alarmIds)
        }
      }
    }
  }

  private fun daysOfWeekToDbString(daysOfWeek: Collection<DayOfWeek>): String? =
    daysOfWeek
      .takeIf(Collection<DayOfWeek>::isNotEmpty)
      ?.sorted()
      ?.joinToString(separator = ",", transform = { it.isoDayNumber.toString() })

  private fun datesToDbString(dates: Collection<LocalDate>): String? =
    dates
      .takeIf(Collection<LocalDate>::isNotEmpty)
      ?.sorted()
      ?.joinToString(separator = ",", transform = LocalDate::toString)
}
