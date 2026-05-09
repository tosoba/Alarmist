package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.usecase.calculateAlarmNextFireOnDateTime
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.alarm.IosAlarmEnvironment
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class IosAlarmScheduler : AlarmScheduler {
  private val notifications = IosAlarmEnvironment.notifications

  override fun scheduleNextWidgetUpdate() {}

  override fun scheduleAlarm(
    id: Long,
    name: String?,
    fireOnDateTime: LocalDateTime,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    soundId: String?,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    // 1. Cancel any existing notifications for this alarm
    cancelAlarm(id)

    // 2. Schedule the primary (first) firing
    notifications.scheduleFiredNotification(
      id = id,
      name = name,
      fireOnDateTime = fireOnDateTime,
      soundId = soundId,
    )

    // 3. Schedule upcoming reminder if needed
    if (reminderOffsetHours > 0L) {
      val upcomingDateTime =
        fireOnDateTime
          .toInstant(TimeZone.currentSystemDefault())
          .minus(reminderOffsetHours, DateTimeUnit.HOUR)
          .toLocalDateTime(TimeZone.currentSystemDefault())

      if (upcomingDateTime > LocalDateTime.now()) {
        notifications.scheduleUpcomingNotification(
          id = id,
          name = name,
          fireOnDateTime = fireOnDateTime,
          upcomingDateTime = upcomingDateTime,
        )
      }
    }

    // 4. Pre-schedule multiple future occurrences for repeating alarms
    // (within iOS limit of 64 pending requests)
    var nextFireOn = fireOnDateTime
    val limit = 14 // Pre-schedule up to 2 weeks if daily, or next 14 occurrences
    repeat(limit) {
      nextFireOn =
        calculateAlarmNextFireOnDateTime(
          fireAtTime = fireOnDateTime.time,
          scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
          scheduledOnDates = scheduledOnDates,
          offOnDates = offOnDates,
          isOn = true,
          afterDateTime = nextFireOn,
        ) ?: return@repeat

      notifications.scheduleFiredNotification(
        id = id,
        name = name,
        fireOnDateTime = nextFireOn,
        soundId = soundId,
      )
    }
  }

  override fun cancelAlarm(id: Long) {
    notifications.cancelAlarmNotifications(id)
  }
}
