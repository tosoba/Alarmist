package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import com.trm.alarmist.db.Alarm
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

const val ALARM_ON = 1L

fun Alarm.toListModel(): AlarmListModel =
  AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn == ALARM_ON,
    nextFireOnDateTime =
      calculateAlarmNextFireOnDateTime(
        isOn = isOn == ALARM_ON,
        fireAtTime = fireAtTime,
        scheduledOnDaysOfWeek = parsedScheduledOnDaysOfWeek(),
        scheduledOnDates = parsedScheduledOnDates(),
        offOnDates = parsedOffOnDates(),
      ),
  )

fun Alarm.toModel(): AlarmModel =
  AlarmModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn == ALARM_ON,
    scheduledOnDaysOfWeek = parsedScheduledOnDaysOfWeek(),
    scheduledOnDates = parsedScheduledOnDates(),
    offOnDates = parsedOffOnDates(),
  )

private fun Alarm?.parsedScheduledOnDaysOfWeek(): List<DayOfWeek> =
  this?.scheduledOnDaysOfWeek?.split(",")?.map { DayOfWeek(isoDayNumber = it.toInt()) }.orEmpty()

private fun Alarm?.parsedScheduledOnDates(): List<LocalDate> =
  this?.scheduledOnDates?.split(",")?.map(LocalDate.Companion::parse).orEmpty()

private fun Alarm?.parsedOffOnDates(): List<LocalDate> =
  this?.offOnDates?.split(",")?.map(LocalDate.Companion::parse).orEmpty()
