package com.trm.alarmist.core.common.util

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListStatus
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import com.trm.alarmist.db.Alarm
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

const val DB_ON = 1L
const val DB_OFF = 0L

fun AlarmModel.toListModel(now: LocalDateTime): AlarmListModel =
  AlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    name = name,
    isOn =
      isOn &&
        (scheduledOnDaysOfWeek.isNotEmpty() ||
          scheduledOnDates.isEmpty() ||
          offOnDates.isEmpty() ||
          !offOnDates.containsAll(scheduledOnDates)),
    fireOnDateTime = calculateNextFireOnDateTime(now),
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
    closestScheduledOnDate =
      scheduledOnDates
        .filter { LocalDateTime(it, fireAtTime) > now && !offOnDates.contains(it) }
        .minOrNull() ?: scheduledOnDates.filter { LocalDateTime(it, fireAtTime) > now }.minOrNull(),
    offOnAllScheduledDates =
      scheduledOnDates.isNotEmpty() && offOnDates.containsAll(scheduledOnDates),
    scheduledOnMultipleDates = isScheduledOnMultipleDates(now),
    snoozedFireAtTime = snoozedFireAtTime,
  )

fun AlarmModel.toUpcomingListModel(
  scheduledAtDate: LocalDate?,
  now: LocalDateTime,
): UpcomingAlarmListModel =
  UpcomingAlarmListModel(
    id = id,
    groupId = groupId,
    fireAtTime = fireAtTime,
    date = scheduledAtDate,
    name = name,
    status =
      when {
        !isOn -> UpcomingAlarmListStatus.OFF
        offOnDates.contains(scheduledAtDate) -> UpcomingAlarmListStatus.OFF_ON_DATE
        else -> UpcomingAlarmListStatus.ON
      },
    fireOnDateTime =
      if (scheduledAtDate != null) {
        when {
          !isOn || offOnDates.contains(scheduledAtDate) -> {
            null
          }
          now.date == scheduledAtDate -> {
            LocalDateTime(date = scheduledAtDate, time = nextFireAtTime)
          }
          else -> {
            LocalDateTime(date = scheduledAtDate, time = fireAtTime)
          }
        }
      } else {
        calculateNextFireOnDateTime(now)
      },
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
    scheduledOnMultipleDates = isScheduledOnMultipleDates(now),
  )

private fun AlarmModel.calculateNextFireOnDateTime(now: LocalDateTime) =
  snoozedFireAtTime?.let { calculateSnoozedNextFireOnDateTime(it, now) }
    ?: run { calculateNonSnoozedNextFireOnDateTime(now) }

private fun AlarmModel.calculateNonSnoozedNextFireOnDateTime(now: LocalDateTime) =
  calculateAlarmNextFireOnDateTime(
    fireAtTime = fireAtTime,
    scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
    scheduledOnDates = scheduledOnDates,
    offOnDates = offOnDates,
    isOn = isOn,
    afterDateTime = lastNotificationDate?.atTime(fireAtTime) ?: now,
  )

private fun AlarmModel.calculateSnoozedNextFireOnDateTime(
  snoozedFireAtTime: LocalTime,
  now: LocalDateTime,
): LocalDateTime? =
  calculateAlarmNextFireOnDateTime(
    fireAtTime = snoozedFireAtTime,
    scheduledOnDaysOfWeek = emptyList(),
    scheduledOnDates = emptyList(),
    offOnDates = emptyList(),
    isOn = isOn,
    afterDateTime = lastNotificationDate?.atTime(fireAtTime) ?: now,
  )

private fun AlarmModel.isScheduledOnMultipleDates(now: LocalDateTime): Boolean =
  scheduledOnDates.count { scheduledOnDate ->
    LocalDateTime(scheduledOnDate, fireAtTime) > now && scheduledOnDate !in offOnDates
  } > 1

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

fun AlarmModel.isCustomScheduledToFireOn(date: LocalDate): Boolean {
  require(!isOneTime)

  return (date.dayOfWeek in scheduledOnDaysOfWeek || date in scheduledOnDates) &&
    date !in offOnDates
}

fun AlarmModel.expectedOneTimeNotificationDateTime(): LocalDateTime {
  require(isOneTime)

  return snoozedFireAtDateTime
    ?: LocalDateTime(
      date =
        if (lastModificationDateTime.time > fireAtTime) {
          lastModificationDateTime.date.plus(1, DateTimeUnit.DAY)
        } else {
          lastModificationDateTime.date
        },
      time = fireAtTime,
    )
}
