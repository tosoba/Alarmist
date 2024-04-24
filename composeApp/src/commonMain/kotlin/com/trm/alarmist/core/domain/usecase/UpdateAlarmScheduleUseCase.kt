package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmModel
import com.trm.alarmist.core.system.AlarmScheduler
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class UpdateAlarmScheduleUseCase(private val scheduler: AlarmScheduler) {
  operator fun invoke(alarm: AlarmModel, afterDateTime: LocalDateTime = LocalDateTime.now()) {
    this(
      isOn = alarm.isOn,
      id = alarm.id,
      fireAtTime = alarm.fireAtTime,
      scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek,
      scheduledOnDates = alarm.scheduledOnDates,
      offOnDates = alarm.offOnDates,
      snoozeAvailable = alarm.snoozeDurationMinutes > 0L && alarm.snoozeCount < alarm.snoozeLimit,
      alarmDurationMinutes = alarm.alarmDurationMinutes,
      soundEnabled = alarm.soundEnabled,
      vibrationEnabled = alarm.vibrationEnabled,
      reminderOffsetHours = alarm.reminderOffsetHours,
      afterDateTime = afterDateTime,
    )
  }

  operator fun invoke(
    isOn: Boolean,
    id: Long,
    fireAtTime: LocalTime,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    snoozeAvailable: Boolean,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    reminderOffsetHours: Long,
    afterDateTime: LocalDateTime = LocalDateTime.now(),
  ) {
    calculateAlarmNextFireOnDateTime(
        fireAtTime = fireAtTime,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
        scheduledOnDates = scheduledOnDates,
        offOnDates = offOnDates,
        isOn = isOn,
        afterDateTime = afterDateTime,
      )
      ?.let {
        scheduler.scheduleAlarm(
          id = id,
          fireOnDateTime = it,
          snoozeAvailable = snoozeAvailable,
          alarmDurationMinutes = alarmDurationMinutes,
          soundEnabled = soundEnabled,
          vibrationEnabled = vibrationEnabled,
          reminderOffsetHours = reminderOffsetHours,
        )
      } ?: run { scheduler.cancelAlarm(id) }
  }
}
