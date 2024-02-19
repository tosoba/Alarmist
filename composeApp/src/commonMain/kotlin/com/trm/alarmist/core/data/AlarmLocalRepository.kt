package com.trm.alarmist.core.data

import com.trm.alarmist.core.common.util.ALARM_ON
import com.trm.alarmist.core.common.util.calculateNextFireOnDateTime
import com.trm.alarmist.core.common.util.nextFireOnDateTime
import com.trm.alarmist.core.common.util.toListModel
import com.trm.alarmist.core.common.util.toModel
import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.db.Alarm
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
    fireAtTime: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    val id =
      db.insertAlarm(
        fireAtTime = fireAtTime,
        name = name,
        isOn = isOn,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
        scheduledOnDates = scheduledOnDates,
        offOnDates = offOnDates,
      )
    scheduleAlarmIfOn(
      isOn = isOn,
      id = id,
      fireAtTime = fireAtTime,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
    )
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
    db.replaceAlarm(
      id = id,
      fireAtTime = fireAtTime,
      name = name,
      isOn = isOn,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
    )
    scheduleAlarmIfOn(
      isOn = isOn,
      id = id,
      fireAtTime = fireAtTime,
      scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
      scheduledOnDates = scheduledOnDates,
      offOnDates = offOnDates,
    )
  }

  private fun scheduleAlarmIfOn(
    isOn: Boolean,
    id: Long,
    fireAtTime: LocalTime,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    if (!isOn) return

    calculateNextFireOnDateTime(
        fireAtTime = fireAtTime,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
        scheduledOnDates = scheduledOnDates,
        offOnDates = offOnDates,
      )
      ?.let { scheduler.scheduleAlarm(id = id, fireOnDateTime = it) }
  }

  override fun getAllAlarmsListFlow(): Flow<List<AlarmListModel>> =
    db.selectAllAlarms().map { alarms -> alarms.map(Alarm::toListModel) }

  override suspend fun getAlarmById(id: Long): AlarmModel = db.selectAlarmById(id).toModel()

  override suspend fun toggleAlarmOnOff(id: Long) {
    val toggledAlarm = db.updateToggleAlarmOnOff(id)
    toggledAlarm
      .takeIf { it.isOn == ALARM_ON }
      ?.nextFireOnDateTime()
      ?.let { scheduler.scheduleAlarm(id = id, fireOnDateTime = it) }
  }
}
