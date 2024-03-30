package com.trm.alarmist.feature.alarms.list

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.common.util.wrapToAny
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListFeature : CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()
  private val toggleAlarmOnOffUseCase: ToggleAlarmOnOffUseCase by inject()

  val alarms: AnyStateFlow<Pair<List<AlarmListModel>, Boolean>> =
    repository
      .getAllAlarmsListFlow()
      .map { it to false }
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList<AlarmListModel>() to true
      )
      .wrapToAny()

  val groups: AnyStateFlow<Map<Long, AlarmGroupModel>> =
    repository
      .getAllAlarmGroupsFlow()
      .map { it.associateBy(AlarmGroupModel::id) }
      .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5_000L), emptyMap())
      .wrapToAny()

  fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    coroutineScope.launch { toggleAlarmOnOffUseCase(alarm.id) }
  }
}
