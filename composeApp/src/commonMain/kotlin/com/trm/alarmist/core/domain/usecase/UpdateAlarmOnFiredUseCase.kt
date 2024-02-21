package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository

class UpdateAlarmOnFiredUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(id: Long) {
    repository.updateAlarmOnFired(id)
  }
}
