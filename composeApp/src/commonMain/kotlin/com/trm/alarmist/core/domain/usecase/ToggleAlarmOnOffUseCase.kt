package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository

class ToggleAlarmOnOffUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
) {
  suspend operator fun invoke(id: Long) {
    updateAlarmScheduleUseCase(repository.toggleAlarmOnOff(id))
  }
}
