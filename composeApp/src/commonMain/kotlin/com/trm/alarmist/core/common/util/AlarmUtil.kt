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
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

const val DB_ON = 1L
const val DB_OFF = 0L

fun Alarm.toListModel(now: LocalDateTime): AlarmListModel {
  val snoozedFireAtTime =
    snoozedFireAtTime(
      lastSnoozedAt = lastSnoozedAt ?: now,
      snoozeDurationMinutes = snoozeDurationMinutes,
      snoozeCount = snoozeCount,
    )
  return AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = isOn == DB_ON,
    fireOnDateTime =
      if (snoozedFireAtTime != null) {
        calculateAlarmNextFireOnDateTime(
          fireAtTime = snoozedFireAtTime,
          scheduledOnDaysOfWeek = emptyList(),
          scheduledOnDates = emptyList(),
          offOnDates = emptyList(),
          isOn = isOn == DB_ON,
          afterDateTime = lastNotificationDate?.atTime(fireAtTime) ?: now,
        )
      } else {
        calculateAlarmNextFireOnDateTime(
          fireAtTime = fireAtTime,
          scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.orEmpty(),
          scheduledOnDates = scheduledOnDates.orEmpty(),
          offOnDates = offOnDates.orEmpty(),
          isOn = isOn == DB_ON,
          afterDateTime = lastNotificationDate?.atTime(fireAtTime) ?: now,
        )
      },
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.orEmpty(),
    scheduledOnClosestDate =
      scheduledOnDates
        ?.run { if (offOnDates.isNullOrEmpty()) this else filter { it !in offOnDates } }
        ?.minOrNull(),
    scheduledOnMultipleDates = (scheduledOnDates?.size ?: 0) - (offOnDates?.size ?: 0) > 1,
    snoozedFireAtTime = snoozedFireAtTime,
  )
}

fun Alarm.toUpcomingListModelScheduledAtDate(date: LocalDate): AlarmListModel =
  AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn = offOnDates.isNullOrEmpty() || !offOnDates.contains(date),
    fireOnDateTime =
      if (offOnDates?.contains(date) == true) null else LocalDateTime(date, fireAtTime),
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.orEmpty(),
    scheduledOnClosestDate = scheduledOnDates?.minOrNull(),
    scheduledOnMultipleDates = (scheduledOnDates?.size ?: 0) - (offOnDates?.size ?: 0) > 1,
    snoozedFireAtTime = null, // currently disregarding snooze info for upcoming alarms
  )

fun snoozedFireAtTime(
  lastSnoozedAt: LocalDateTime,
  snoozeDurationMinutes: Long,
  snoozeCount: Long,
): LocalTime? =
  (snoozeCount * snoozeDurationMinutes)
    .takeIf { it > 0L }
    ?.let { lastSnoozedAt.toInstant(TimeZone.currentSystemDefault()).plus(it, DateTimeUnit.MINUTE) }
    ?.toLocalTimeDefault()

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
    snoozeDurationMinutes = snoozeDurationMinutes,
    snoozeCount = snoozeCount,
    lastSnoozedAt = lastSnoozedAt,
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
