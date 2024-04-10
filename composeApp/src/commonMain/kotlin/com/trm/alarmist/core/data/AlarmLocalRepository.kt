package com.trm.alarmist.core.data

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.trm.alarmist.core.common.util.DB_OFF
import com.trm.alarmist.core.common.util.DB_ON
import com.trm.alarmist.core.common.util.expectedOneTimeNotificationDateTime
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toAlarmScheduleModel
import com.trm.alarmist.core.common.util.toListModel
import com.trm.alarmist.core.common.util.toModel
import com.trm.alarmist.core.common.util.toUpcomingListModelScheduledAtDate
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.model.AlarmScheduleModel
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.AlarmistQueries
import com.trm.alarmist.db.SelectAllGroups
import com.trm.alarmist.db.SelectOnAlarmSchedules
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus

class AlarmLocalRepository(
  private val queries: AlarmistQueries,
  private val dispatcher: CoroutineDispatcher,
) : AlarmRepository {
  override suspend fun addAlarm(
    groupId: Long?,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    snoozeDurationMinutes: Long,
  ): Long =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.insertAlarm(
          id = null,
          groupId = groupId,
          fireAtTime = fireAtTime,
          name = name,
          isOn = if (isOn) 1L else 0L,
          scheduledOnDaysOfWeek =
            scheduledOnDaysOfWeek.takeIf(Collection<DayOfWeek>::isNotEmpty)?.toList(),
          scheduledOnDates = scheduledOnDates.takeIf(Collection<LocalDate>::isNotEmpty)?.toList(),
          offOnDates = offOnDates.takeIf(Collection<LocalDate>::isNotEmpty)?.toList(),
          lastModificationDateTime = LocalDateTime.now(),
          lastNotificationDate = null,
          snoozeDurationMinutes = snoozeDurationMinutes,
          snoozeCount = 0,
          lastSnoozedAt = null,
        )
        queries.selectLastInsertedRowId().executeAsOne()
      }
    }

  override suspend fun editAlarm(
    id: Long,
    groupId: Long?,
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    snoozeDurationMinutes: Long,
  ) {
    withContext(dispatcher) {
      queries.updateAlarmById(
        id = id,
        groupId = groupId,
        fireAtTime = fireAtTime,
        name = name,
        isOn = if (isOn) 1L else 0L,
        scheduledOnDaysOfWeek =
          scheduledOnDaysOfWeek.takeIf(Collection<DayOfWeek>::isNotEmpty)?.toList(),
        scheduledOnDates = scheduledOnDates.takeIf(Collection<LocalDate>::isNotEmpty)?.toList(),
        offOnDates = offOnDates.takeIf(Collection<LocalDate>::isNotEmpty)?.toList(),
        lastModificationDateTime = LocalDateTime.now(),
        snoozeDurationMinutes = snoozeDurationMinutes,
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

  override suspend fun getOnAlarmsAndResetMissedAlarms(): List<AlarmModel> {
    val now = LocalDateTime.now()
    return withContext(dispatcher) {
      queries.transactionWithResult {
        val onAlarms = queries.selectOnAlarms().executeAsList().map(Alarm::toModel)

        // Reset missed one time alarms
        onAlarms
          .filter { it.scheduledOnDaysOfWeek.isEmpty() && it.scheduledOnDates.isEmpty() }
          .map { it to it.expectedOneTimeNotificationDateTime() }
          .filter { (alarm, expectedNotificationDateTime) ->
            now > expectedNotificationDateTime &&
              alarm.lastNotificationDate != expectedNotificationDateTime.date
          }
          .map { (alarm) -> alarm.id }
          .takeIf(List<Long>::isNotEmpty)
          ?.let { queries.updateResetAlarmByIds(now, it) }

        // Reset alarms that are scheduled on past dates only
        queries
          .selectOnAlarmsOnlyScheduledOnDates()
          .executeAsList()
          .map { it.id to it.scheduledOnDates }
          .filter { (_, scheduledOnDate) ->
            scheduledOnDate.isNotEmpty() && scheduledOnDate.last() < now.date
          }
          .map { (id) -> id }
          .takeIf(List<Long>::isNotEmpty)
          ?.let { queries.updateResetAlarmByIds(now, it) }

        queries.updateOnAlarmsLastModificationDateTime(now)

        onAlarms
      }
    }
  }

  override fun getOnAlarmsScheduledToFireOnDate(date: LocalDate): Flow<List<AlarmListModel>> =
    queries
      .selectOnAlarmsScheduledToFireOnDate(
        date = date.toString(),
        dayOfWeek = date.dayOfWeek.isoDayNumber.toString(),
      )
      .asAlarmsListFlow { it.toUpcomingListModelScheduledAtDate(date) }

  override fun getOnAlarmSchedulesForDates(
    dates: ClosedRange<LocalDate>
  ): Flow<List<AlarmScheduleModel>> =
    queries.selectOnAlarmSchedules(dates.toQueryString()).asFlow().mapToList(dispatcher).map {
      it.map(SelectOnAlarmSchedules::toAlarmScheduleModel)
    }

  private fun ClosedRange<LocalDate>.toQueryString(): String =
    List(endInclusive.toEpochDays() - start.toEpochDays() + 1) { start.plus(it, DateTimeUnit.DAY) }
      .joinToString { it.toString().replace(".", "\\.") }

  override fun getOnOneTimeAlarmsBeforeTime(time: LocalTime): Flow<List<AlarmListModel>> =
    queries.selectOnOneTimeAlarmsBeforeTime(time).asAlarmsListFlow()

  override fun getOnOneTimeAlarmsAfterTime(time: LocalTime): Flow<List<AlarmListModel>> =
    queries.selectOnOneTimeAlarmsAfterTime(time).asAlarmsListFlow()

  override fun countOnOneTimeAlarmsBeforeTime(time: LocalTime): Flow<Int> =
    queries.selectCountOneTimeAlarmsBeforeTime(time).asFlow().mapToOne(dispatcher).map(Long::toInt)

  override fun countOnOneTimeAlarmsAfterTime(time: LocalTime): Flow<Int> =
    queries.selectCountOneTimeAlarmsAfterTime(time).asFlow().mapToOne(dispatcher).map(Long::toInt)

  private fun Query<Alarm>.asAlarmsListFlow(
    now: LocalDateTime = LocalDateTime.now(),
    mapper: (Alarm) -> AlarmListModel = { it.toListModel(now) },
  ): Flow<List<AlarmListModel>> {
    return asFlow().mapToList(dispatcher).map { it.map(mapper) }
  }

  override suspend fun toggleAlarmOnOff(id: Long): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateToggleAlarmOnOffById(LocalDateTime.now(), id)
        queries.selectAlarmById(id).executeAsOne().toModel()
      }
    }

  override suspend fun toggleAlarmOnOffOnDate(id: Long, date: LocalDate): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        val alarm = queries.selectAlarmById(id).executeAsOne()
        when {
          alarm.scheduledOnDaysOfWeek.isNullOrEmpty() && alarm.scheduledOnDates.isNullOrEmpty() -> {
            queries.updateToggleAlarmOnOffById(LocalDateTime.now(), id)
          }
          alarm.offOnDates?.contains(date) == true -> {
            queries.updateOffOnDatesById(
              offOnDates = (alarm.offOnDates - date).takeIf(Collection<LocalDate>::isNotEmpty),
              lastModificationDateTime = LocalDateTime.now(),
              id = id,
            )
          }
          else -> {
            queries.updateOffOnDatesById(
              offOnDates = alarm.offOnDates?.let { it + date } ?: listOf(date),
              lastModificationDateTime = LocalDateTime.now(),
              id = id,
            )
          }
        }
        queries.selectAlarmById(id).executeAsOne().toModel()
      }
    }

  override suspend fun updateGroupAlarmsOnOff(groupId: Long, isOn: Boolean): List<AlarmModel> =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateAlarmsOnOffByGroupId(
          isOn = if (isOn) DB_ON else DB_OFF,
          lastModificationDateTime = LocalDateTime.now(),
          groupId = groupId,
        )
        queries.selectAlarmsByGroupId(groupId).executeAsList().map(Alarm::toModel)
      }
    }

  override suspend fun updateUngroupedAlarmsOnOff(isOn: Boolean): List<AlarmModel> =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateUngroupedAlarmsOnOff(
          isOn = if (isOn) DB_ON else DB_OFF,
          lastModificationDateTime = LocalDateTime.now(),
        )
        queries.selectUngroupedAlarms().executeAsList().map(Alarm::toModel)
      }
    }

  override suspend fun getAlarmById(id: Long): AlarmModel =
    withContext(dispatcher) { queries.selectAlarmById(id).executeAsOne().toModel() }

  override suspend fun updateAlarmOnDismiss(
    id: Long,
    notificationDateTime: LocalDateTime,
  ): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateAlarmLastNotificationDateById(notificationDateTime.date, id)
        val alarm = queries.selectAlarmById(id).executeAsOne().toModel()
        if (
          alarm.scheduledOnDaysOfWeek.isEmpty() &&
            (alarm.scheduledOnDates.isEmpty() ||
              alarm.scheduledOnDates.lastOrNull() == notificationDateTime.date)
        ) {
          queries.updateResetAlarmById(LocalDateTime.now(), id)
          queries.selectAlarmById(id).executeAsOne().toModel()
        } else if (notificationDateTime.date in alarm.scheduledOnDates) {
          queries.updateScheduledOnDatesById(
            scheduledOnDates =
              (alarm.scheduledOnDates - notificationDateTime.date).takeIf(
                Collection<LocalDate>::isNotEmpty
              ),
            lastModificationDateTime = notificationDateTime,
            id = id,
          )
          queries.selectAlarmById(id).executeAsOne().toModel()
        } else {
          alarm
        }
      }
    }

  override suspend fun updateAlarmOnSnooze(id: Long): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateAlarmSnoozeById(LocalDateTime.now(), id)
        queries.selectAlarmById(id).executeAsOne().toModel()
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
}
