package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toListModel
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime

class GetAlarmsInGroupFlowUseCase(private val repository: AlarmRepository) {
  operator fun invoke(groupId: Long): Flow<List<AlarmListModel>> =
    if (groupId == AlarmGroupModel.UNGROUPED_ID) {
        repository.getUngroupedAlarmsFlow()
      } else {
        repository.getAlarmsInGroupFlow(groupId)
      }
      .map { alarms ->
        val now = LocalDateTime.now()
        alarms.map { it.toListModel(now) }
      }
}
