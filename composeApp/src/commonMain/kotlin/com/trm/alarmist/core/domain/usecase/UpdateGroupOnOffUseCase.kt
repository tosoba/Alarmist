package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.WidgetManager

class UpdateGroupOnOffUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
  private val widgetManager: WidgetManager,
) {
  suspend operator fun invoke(id: Long, isOn: Boolean) {
    if (id == AlarmGroupModel.UNGROUPED_ID) {
        repository.updateUngroupedAlarmsOnOff(isOn)
      } else {
        repository.updateGroupAlarmsOnOff(id, isOn)
      }
      .forEach { alarm ->
        calculateAlarmNextFireOnDateTime(alarm)?.let {
          scheduler.scheduleAlarm(
            id = alarm.id,
            name = alarm.name,
            fireOnDateTime = it,
            alarmDurationMinutes = alarm.alarmDurationMinutes,
            soundEnabled = alarm.soundEnabled,
            soundId = alarm.soundId,
            vibrationEnabled = alarm.vibrationEnabled,
            reminderOffsetHours = alarm.reminderOffsetHours,
            scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek,
            scheduledOnDates = alarm.scheduledOnDates,
            offOnDates = alarm.offOnDates,
          )
        } ?: run { scheduler.cancelAlarm(alarm.id) }
      }

    widgetManager.updateAllWidgets()
  }
}
