package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import com.trm.alarmist.db.Alarm
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

const val DB_ON = 1L
const val DB_OFF = 0L

fun Alarm.toListModel(): AlarmListModel {
  val parsedScheduledOnDaysOfWeek = parsedScheduledOnDaysOfWeek()
  val parsedScheduledOnDates = parsedScheduledOnDates()
  return AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn == DB_ON,
    nextFireOnDateTime =
      calculateAlarmNextFireOnDateTime(
        isOn = isOn == DB_ON,
        fireAtTime = fireAtTime,
        scheduledOnDaysOfWeek = parsedScheduledOnDaysOfWeek,
        scheduledOnDates = parsedScheduledOnDates,
        offOnDates = parsedOffOnDates(),
      ),
    scheduleDescription =
      if (parsedScheduledOnDaysOfWeek.isNotEmpty() || parsedScheduledOnDates.isNotEmpty()) {
        (parsedScheduledOnDaysOfWeek.map { it.name.take(2) } +
            if (parsedScheduledOnDates.isNotEmpty()) listOf("Other") else emptyList())
          .joinToString(" ")
      } else {
        "Everyday"
      },
  )
}

fun Alarm.toModel(): AlarmModel =
  AlarmModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn == DB_ON,
    scheduledOnDaysOfWeek = parsedScheduledOnDaysOfWeek(),
    scheduledOnDates = parsedScheduledOnDates(),
    offOnDates = parsedOffOnDates(),
    lastNotificationDate = lastNotificationDate,
  )

private fun Alarm?.parsedScheduledOnDaysOfWeek(): List<DayOfWeek> =
  this?.scheduledOnDaysOfWeek?.split(",")?.map { DayOfWeek(isoDayNumber = it.toInt()) }.orEmpty()

private fun Alarm?.parsedScheduledOnDates(): List<LocalDate> =
  this?.scheduledOnDates?.split(",")?.map(LocalDate.Companion::parse).orEmpty()

private fun Alarm?.parsedOffOnDates(): List<LocalDate> =
  this?.offOnDates?.split(",")?.map(LocalDate.Companion::parse).orEmpty()
