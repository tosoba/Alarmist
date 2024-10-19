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
      name = alarm.name,
      fireAtTime = alarm.fireAtTime,
      scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek,
      scheduledOnDates = alarm.scheduledOnDates,
      offOnDates = alarm.offOnDates,
      alarmDurationMinutes = alarm.alarmDurationMinutes,
      soundEnabled = alarm.soundEnabled,
      soundId = alarm.soundId,
      vibrationEnabled = alarm.vibrationEnabled,
      reminderOffsetHours = alarm.reminderOffsetHours,
      afterDateTime = afterDateTime,
    )
  }

  operator fun invoke(
    isOn: Boolean,
    id: Long,
    name: String?,
    fireAtTime: LocalTime,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
    alarmDurationMinutes: Long,
    soundEnabled: Boolean,
    soundId: String?,
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
          name = name,
          fireOnDateTime = it,
          alarmDurationMinutes = alarmDurationMinutes,
          soundEnabled = soundEnabled,
          soundId = soundId,
          vibrationEnabled = vibrationEnabled,
          reminderOffsetHours = reminderOffsetHours,
        )
      } ?: run { scheduler.cancelAlarm(id) }
  }
}
