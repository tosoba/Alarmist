package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository

class UpdateAlarmOnDismissedUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(id: Long) {
    repository.updateAlarmOnDismissed(id)
  }
}
