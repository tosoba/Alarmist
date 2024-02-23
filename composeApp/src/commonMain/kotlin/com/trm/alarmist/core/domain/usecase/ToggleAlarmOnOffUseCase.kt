package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler

class ToggleAlarmOnOffUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long) {
    val toggledAlarm = repository.toggleAlarmOnOff(id)
    if (toggledAlarm.isOn) {
      calculateAlarmNextFireOnDateTime(toggledAlarm)?.let {
        scheduler.scheduleAlarm(id = id, fireOnDateTime = it)
      }
    } else {
      scheduler.cancelAlarm(id)
    }
  }
}
