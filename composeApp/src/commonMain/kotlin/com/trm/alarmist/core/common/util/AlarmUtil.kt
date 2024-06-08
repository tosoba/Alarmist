package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.model.AlarmScheduleModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListStatus
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
    snoozedFireAtTime(lastSnoozedAt = lastSnoozedAt, snoozeDurationMinutes = snoozeDurationMinutes)
  return AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn =
      isOn == DB_ON &&
        (!scheduledOnDaysOfWeek.isNullOrEmpty() ||
          scheduledOnDates.isNullOrEmpty() ||
          offOnDates.isNullOrEmpty() ||
          !offOnDates.containsAll(scheduledOnDates)),
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
    closestScheduledOnDate =
      scheduledOnDates
        ?.filter {
          (it > now.date || (it == now.date && fireAtTime > now.time)) &&
            (offOnDates.isNullOrEmpty() || it !in offOnDates)
        }
        ?.minOrNull()
        ?: scheduledOnDates
          ?.filter { it > now.date || (it == now.date && fireAtTime > now.time) }
          ?.minOrNull(),
    offOnAllScheduledDates =
      !scheduledOnDates.isNullOrEmpty() && offOnDates?.containsAll(scheduledOnDates) == true,
    scheduledOnMultipleDates = isScheduledOnMultipleDates(),
    snoozedFireAtTime = snoozedFireAtTime,
  )
}

fun Alarm.toUpcomingListModelScheduledAtDate(
  date: LocalDate?,
  now: LocalDateTime,
): UpcomingAlarmListModel =
  UpcomingAlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    date = date,
    name = name,
    status =
      when {
        isOn != DB_ON -> {
          UpcomingAlarmListStatus.OFF
        }
        !offOnDates.isNullOrEmpty() && offOnDates.contains(date) -> {
          UpcomingAlarmListStatus.OFF_ON_DATE
        }
        else -> {
          UpcomingAlarmListStatus.ON
        }
      },
    fireOnDateTime =
      if (date != null) {
        LocalDateTime(date, fireAtTime)
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
    scheduledOnMultipleDates = isScheduledOnMultipleDates(),
  )

private fun Alarm.isScheduledOnMultipleDates(): Boolean =
  (scheduledOnDates?.count(
    if (offOnDates == null) { _ -> true } else { scheduledOnDate -> scheduledOnDate !in offOnDates }
  ) ?: 0) > 1

fun snoozedFireAtTime(lastSnoozedAt: LocalDateTime?, snoozeDurationMinutes: Long): LocalTime? =
  if (lastSnoozedAt != null && snoozeDurationMinutes > 0L) {
    lastSnoozedAt
      .toInstant(TimeZone.currentSystemDefault())
      .plus(snoozeDurationMinutes, DateTimeUnit.MINUTE)
      .toLocalTime()
  } else {
    null
  }

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
    snoozeLimit = snoozeLimit,
    lastSnoozedAt = lastSnoozedAt,
    alarmDurationMinutes = alarmDurationMinutes,
    soundEnabled = soundEnabled == DB_ON,
    vibrationEnabled = vibrationEnabled == DB_ON,
    reminderOffsetHours = reminderOffsetHours,
    soundId = soundId,
  )

fun SelectOnAlarmSchedules.toAlarmScheduleModel(): AlarmScheduleModel =
  AlarmScheduleModel(
    id = id,
    fireAtTime = fireAtTime,
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek.orEmpty().toSet(),
    scheduledOnDates = scheduledOnDates.orEmpty().toSet(),
    offOnDates = offOnDates.orEmpty().toSet(),
  )

fun AlarmModel.isScheduledToFireOn(date: LocalDate): Boolean {
  require(!isOneTime)

  return (date.dayOfWeek in scheduledOnDaysOfWeek || date in scheduledOnDates) &&
    date !in offOnDates
}

fun AlarmModel.expectedOneTimeNotificationDateTime(): LocalDateTime {
  require(isOneTime)

  return LocalDateTime(
    date =
      if (lastModificationDateTime.time > fireAtTime) {
        lastModificationDateTime.date.plus(1, DateTimeUnit.DAY)
      } else {
        lastModificationDateTime.date
      },
    time =
      snoozedFireAtTime(
        lastSnoozedAt = lastSnoozedAt,
        snoozeDurationMinutes = snoozeDurationMinutes,
      ) ?: fireAtTime,
  )
}
