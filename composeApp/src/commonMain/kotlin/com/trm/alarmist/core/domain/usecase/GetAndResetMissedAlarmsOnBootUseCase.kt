package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus

class GetAndResetMissedAlarmsOnBootUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(): Map<AlarmModel, List<LocalDateTime>> =
    repository.getOnAlarmsAndResetMissedAlarms().let(::calculateMissedAlarmsDateTimes)
}
