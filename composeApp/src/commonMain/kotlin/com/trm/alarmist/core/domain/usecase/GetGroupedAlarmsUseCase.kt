package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.GroupedAlarmsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetGroupedAlarmsUseCase(private val repository: AlarmRepository) {
  operator fun invoke(): Flow<GroupedAlarmsModel> =
    repository.getAllAlarmGroupsFlow().combine(repository.getAllAlarmsListFlow()) { groups, alarms
      ->
      GroupedAlarmsModel(
        alarms = alarms.groupBy { it.groupId ?: -1 },
        groups = groups.associateBy(AlarmGroupModel::id),
      )
    }
}
