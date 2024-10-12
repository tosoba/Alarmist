package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.WidgetManager

class UpdateAlarmOnSnoozeUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
  private val widgetManager: WidgetManager,
) {
  suspend operator fun invoke(id: Long) {
    val alarm = repository.updateAlarmOnSnooze(id)
    alarm.snoozedFireAtDateTime?.let {
      scheduler.scheduleAlarm(
        id = id,
        name = alarm.name,
        fireOnDateTime = it,
        snoozeAvailable = alarm.snoozeCount < alarm.snoozeLimit,
        alarmDurationMinutes = alarm.alarmDurationMinutes,
        soundEnabled = alarm.soundEnabled,
        soundId = alarm.soundId,
        vibrationEnabled = alarm.vibrationEnabled,
        reminderOffsetHours = alarm.reminderOffsetHours,
      )
    }

    widgetManager.updateAllWidgets()
  }
}
