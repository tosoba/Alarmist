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
      val groupedAlarms = alarms.groupBy { it.groupId ?: AlarmGroupModel.UNGROUPED_ID }
      GroupedAlarmsModel(
        alarms = groupedAlarms,
        groups =
          groups.associateBy(AlarmGroupModel::id) +
            mapOf(
              AlarmGroupModel.UNGROUPED_ID to
                AlarmGroupModel(
                  id = AlarmGroupModel.UNGROUPED_ID,
                  name = AlarmGroupModel.UNGROUPED_NAME,
                  color = AlarmGroupModel.TRANSPARENT_COLOR,
                  alarmsCount = groupedAlarms[AlarmGroupModel.UNGROUPED_ID]?.size?.toLong() ?: 0L,
                )
            ),
      )
    }
}
