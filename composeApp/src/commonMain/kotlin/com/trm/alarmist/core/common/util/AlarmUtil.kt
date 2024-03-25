package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.model.AlarmScheduleModel
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.SelectOnAlarmSchedules
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

const val DB_ON = 1L
const val DB_OFF = 0L

fun Alarm.toListModel(): AlarmListModel =
  AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn == DB_ON,
    fireOnDateTime =
      calculateAlarmNextFireOnDateTime(
        fireAtTime = fireAtTime,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.orEmpty(),
        scheduledOnDates = scheduledOnDates.orEmpty(),
        offOnDates = offOnDates.orEmpty(),
        isOn = isOn == DB_ON,
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

fun Alarm.toUpcomingListModelScheduledAtDate(date: LocalDate): AlarmListModel =
  AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = offOnDates.isNullOrEmpty() || !offOnDates.contains(date),
    fireOnDateTime =
      if (offOnDates?.contains(date) == true) null else LocalDateTime(date, fireAtTime),
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

fun AlarmModel.isScheduledToFireOn(date: LocalDate): Boolean {
  require(scheduledOnDaysOfWeek.isNotEmpty() || scheduledOnDates.isNotEmpty())
  return (date.dayOfWeek in scheduledOnDaysOfWeek || date in scheduledOnDates) &&
    date !in offOnDates
}

fun AlarmModel.expectedOneTimeNotificationDateTime(): LocalDateTime {
  require(scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty())
  return LocalDateTime(
    date =
      if (lastModificationDateTime.time > fireAtTime) {
        lastModificationDateTime.date.plus(1, DateTimeUnit.DAY)
      } else {
        lastModificationDateTime.date
      },
    time = fireAtTime,
  )
}
