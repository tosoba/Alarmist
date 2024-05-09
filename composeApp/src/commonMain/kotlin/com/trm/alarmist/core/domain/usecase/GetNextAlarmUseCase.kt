package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel

class GetNextAlarmUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(): AlarmListModel? =
    repository
      .getAllOnAlarmsList()
      .filter { it.fireOnDateTime != null }
      .minByOrNull { it.fireOnDateTime!! }
}
