package com.trm.alarmist.core.data

import com.trm.alarmist.core.common.util.ALARM_ON
import com.trm.alarmist.core.common.util.calculateNextFireOnDateTime
import com.trm.alarmist.core.common.util.nextFireOnDateTime
import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListItem
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class AlarmLocalRepository(
  private val db: AlarmistDatabase,
  private val scheduler: AlarmScheduler,
) : AlarmRepository {
  override suspend fun addAlarm(
    fireAt: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    val id =
      db.insertAlarm(
        fireAt = fireAt,
        name = name,
        isOn = isOn,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
        scheduledOnDates = scheduledOnDates,
        offOnDates = offOnDates,
      )
    isOn
      .takeIf { it }
      ?.let {
        calculateNextFireOnDateTime(
          fireAt = fireAt,
          scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
          scheduledOnDates = scheduledOnDates,
          offOnDates = offOnDates,
        )
      }
      ?.let { scheduler.scheduleAlarm(id = id, fireOnDateTime = it) }
  }

  override fun getAllAlarms(): Flow<List<AlarmListItem>> =
    db.selectAllAlarms().map { alarms ->
      alarms.map {
        AlarmListItem(
          id = it.id,
          fireAt = it.fireAt,
          name = it.name,
          isOn = it.isOn == ALARM_ON,
          nextFireOnDateTime = it.nextFireOnDateTime(),
        )
      }
    }

  override suspend fun toggleAlarmOnOff(id: Long) {
    val toggledAlarm = db.updateToggleAlarmOnOff(id)
    toggledAlarm
      .takeIf { it.isOn == ALARM_ON }
      ?.nextFireOnDateTime()
      ?.let { scheduler.scheduleAlarm(id = id, fireOnDateTime = it) }
  }
}
