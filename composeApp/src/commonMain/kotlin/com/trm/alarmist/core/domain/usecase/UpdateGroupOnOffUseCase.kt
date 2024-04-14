package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.system.AlarmScheduler

class UpdateGroupOnOffUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long, isOn: Boolean) {
    val updatedAlarms =
      if (id == AlarmGroupModel.UNGROUPED_ID) {
        repository.updateUngroupedAlarmsOnOff(isOn)
      } else {
        repository.updateGroupAlarmsOnOff(id, isOn)
      }
    updatedAlarms.forEach { alarm ->
      calculateAlarmNextFireOnDateTime(alarm)?.let {
        scheduler.scheduleAlarm(
          id = alarm.id,
          fireOnDateTime = it,
          snoozeAvailable = alarm.snoozeDurationMinutes > 0L,
        )
      } ?: run { scheduler.cancelAlarm(alarm.id) }
    }
  }
}
