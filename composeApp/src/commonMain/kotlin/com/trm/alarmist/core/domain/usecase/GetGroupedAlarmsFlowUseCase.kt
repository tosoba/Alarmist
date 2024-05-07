package com.trm.alarmist.core.domain.usecase

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.ungrouped
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.GroupedAlarmsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString

class GetGroupedAlarmsFlowUseCase(private val repository: AlarmRepository) {
  @OptIn(ExperimentalResourceApi::class)
  operator fun invoke(): Flow<GroupedAlarmsModel> =
    repository.getAllAlarmGroupsFlow().combine(repository.getAllAlarmsListFlow()) { groups, alarms
      ->
      GroupedAlarmsModel(
        alarms = alarms.groupBy { it.groupId ?: AlarmGroupModel.UNGROUPED_ID },
        groups =
          groups.associateBy(AlarmGroupModel::id) +
            mapOf(
              AlarmGroupModel.UNGROUPED_ID to
                AlarmGroupModel(
                  id = AlarmGroupModel.UNGROUPED_ID,
                  name = getString(Res.string.ungrouped),
                  color = 0L,
                  alarmsCount = alarms.count { it.groupId == null }.toLong(),
                  isOn = alarms.any { it.groupId == null && it.isOn },
                )
            ),
      )
    }
}
