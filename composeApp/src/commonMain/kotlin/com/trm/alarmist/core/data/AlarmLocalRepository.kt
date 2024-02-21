package com.trm.alarmist.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.trm.alarmist.core.common.util.toListModel
import com.trm.alarmist.core.common.util.toModel
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.AlarmistQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
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
          firedCount = 0L,
          dismissedCount = 0L,
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
    queries.selectAllAlarms().asFlow().mapToList(dispatcher).map { alarms ->
      alarms.map(Alarm::toListModel)
    }

  override suspend fun toggleAlarmOnOff(id: Long): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateToggleAlarmOnOffById(id)
        queries.selectAlarmById(id).executeAsOne().toModel()
      }
    }

  override suspend fun getAlarmById(id: Long): AlarmModel =
    withContext(dispatcher) { queries.selectAlarmById(id).executeAsOne().toModel() }

  override suspend fun updateAlarmOnFired(id: Long): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateAlarmFiredCountById(id)
        queries.selectAlarmById(id).executeAsOne().toModel()
      }
    }

  override suspend fun updateAlarmOnDismissed(id: Long): AlarmModel =
    withContext(dispatcher) {
      queries.transactionWithResult {
        queries.updateAlarmDismissedCountById(id)
        queries.selectAlarmById(id).executeAsOne().toModel()
      }
    }

  private fun daysOfWeekToDbString(daysOfWeek: Collection<DayOfWeek>): String? =
    daysOfWeek
      .takeIf(Collection<DayOfWeek>::isNotEmpty)
      ?.joinToString(separator = ",", transform = { it.isoDayNumber.toString() })

  private fun datesToDbString(dates: Collection<LocalDate>): String? =
    dates
      .takeIf(Collection<LocalDate>::isNotEmpty)
      ?.joinToString(separator = ",", transform = LocalDate::toString)
}
