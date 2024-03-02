package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository

class UpdateAlarmOnDismissedUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
) {
  suspend operator fun invoke(id: Long) {
    updateAlarmScheduleUseCase(repository.updateAlarmOnDismissed(id))
  }
}
