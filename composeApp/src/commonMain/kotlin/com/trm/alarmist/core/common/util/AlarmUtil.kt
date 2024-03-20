package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.model.AlarmScheduleModel
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.SelectOnAlarmSchedules
import kotlinx.datetime.LocalDate

const val DB_ON = 1L
const val DB_OFF = 0L

fun Alarm.toListModel(): AlarmListModel =
  AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn == DB_ON,
    nextFireOnDateTime =
      calculateAlarmNextFireOnDateTime(
        isOn = isOn == DB_ON,
        fireAtTime = fireAtTime,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.orEmpty(),
        scheduledOnDates = scheduledOnDates.orEmpty(),
        offOnDates = offOnDates.orEmpty(),
      ),
    scheduleDescription =
      if (!scheduledOnDaysOfWeek.isNullOrEmpty() || !scheduledOnDates.isNullOrEmpty()) {
        (scheduledOnDaysOfWeek?.map { it.name.take(2) }.orEmpty() +
            if (!scheduledOnDates.isNullOrEmpty()) listOf("Other") else emptyList())
          .joinToString(" ")
      } else {
        "One time"
      },
  )

fun Alarm.toModel(): AlarmModel =
  AlarmModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn == DB_ON,
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.orEmpty(),
    scheduledOnDates = scheduledOnDates.orEmpty(),
    offOnDates = offOnDates.orEmpty(),
    lastModificationDateTime = lastModificationDateTime,
    lastNotificationDate = lastNotificationDate,
  )

fun SelectOnAlarmSchedules.toAlarmScheduleModel(): AlarmScheduleModel =
  AlarmScheduleModel(
    id = id,
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.orEmpty().toSet(),
    scheduledOnDates = scheduledOnDates.orEmpty().toSet(),
    offOnDates = offOnDates.orEmpty().toSet(),
  )

fun AlarmModel.shouldFireOn(date: LocalDate): Boolean =
  firesEveryDay ||
    ((date.dayOfWeek in scheduledOnDaysOfWeek || date in scheduledOnDates) && date !in offOnDates)

private val AlarmModel.firesEveryDay: Boolean
  get() = scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty()
