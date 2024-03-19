package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.model.AlarmScheduleModel
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.SelectOnAlarmSchedules
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

const val DB_ON = 1L
const val DB_OFF = 0L

fun Alarm.toListModel(): AlarmListModel {
  val parsedScheduledOnDaysOfWeek = scheduledOnDaysOfWeek.parsedDaysOfWeek()
  val parsedScheduledOnDates = scheduledOnDates.parsedDates()
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
        offOnDates = offOnDates.parsedDates(),
      ),
    scheduleDescription =
      if (parsedScheduledOnDaysOfWeek.isNotEmpty() || parsedScheduledOnDates.isNotEmpty()) {
        (parsedScheduledOnDaysOfWeek.map { it.name.take(2) } +
            if (parsedScheduledOnDates.isNotEmpty()) listOf("Other") else emptyList())
          .joinToString(" ")
      } else {
        "One time"
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
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.parsedDaysOfWeek(),
    scheduledOnDates = scheduledOnDates.parsedDates(),
    offOnDates = offOnDates.parsedDates(),
    lastModificationDateTime = lastModificationDateTime,
    lastNotificationDate = lastNotificationDate,
  )

fun SelectOnAlarmSchedules.toAlarmScheduleModel(): AlarmScheduleModel =
  AlarmScheduleModel(
    id = id,
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.parsedDaysOfWeek().toSet(),
    scheduledOnDates = scheduledOnDates.parsedDates().toSet(),
    offOnDates = offOnDates.parsedDates().toSet(),
  )

private fun String?.parsedDaysOfWeek(): List<DayOfWeek> =
  this?.split(",")?.map { DayOfWeek(isoDayNumber = it.toInt()) }.orEmpty()

private fun String?.parsedDates(): List<LocalDate> =
  this?.split(",")?.map(LocalDate.Companion::parse).orEmpty()

fun AlarmModel.shouldFireOn(date: LocalDate): Boolean =
  firesEveryDay ||
    ((date.dayOfWeek in scheduledOnDaysOfWeek || date in scheduledOnDates) && date !in offOnDates)

private val AlarmModel.firesEveryDay: Boolean
  get() = scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty()
