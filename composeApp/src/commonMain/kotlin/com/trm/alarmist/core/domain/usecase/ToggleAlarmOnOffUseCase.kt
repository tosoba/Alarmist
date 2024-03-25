package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler

class ToggleAlarmOnOffUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
) {
  suspend operator fun invoke(id: Long) {
    val toggledAlarm = repository.toggleAlarmOnOff(id)
    calculateAlarmNextFireOnDateTime(toggledAlarm)?.let {
      scheduler.scheduleAlarm(id = id, fireOnDateTime = it)
    } ?: run { scheduler.cancelAlarm(id) }
  }
}
