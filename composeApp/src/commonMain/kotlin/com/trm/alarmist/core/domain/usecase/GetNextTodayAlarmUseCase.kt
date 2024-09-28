package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.toListModel
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.datetime.LocalDateTime

class GetNextTodayAlarmUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(now: LocalDateTime): AlarmListModel? =
    repository
      .getAllOnAlarmsList()
      .map { it.toListModel(now) }
      .filter {
        it.fireOnDateTime != null && it.fireOnDateTime > now && it.fireOnDateTime.date == now.date
      }
      .minByOrNull { it.fireOnDateTime!! }
}
