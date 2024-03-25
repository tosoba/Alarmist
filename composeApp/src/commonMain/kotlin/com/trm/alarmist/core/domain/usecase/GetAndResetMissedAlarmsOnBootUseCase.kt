package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.LocalDateTime

class GetAndResetMissedAlarmsOnBootUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(): Map<AlarmModel, List<LocalDateTime>> {
    val now = LocalDateTime.now()
    return repository
      .getOnAlarmsAndResetMissedAlarms()
      .associateWith { calculateAlarmMissedDateTimes(it, now) }
      .filterValues(List<LocalDateTime>::isNotEmpty)
  }
}
