package com.trm.alarmist.feature.alarms.groups

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.common.util.wrapToAny
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsInGroupFlowUseCase
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmGroupsFeature(savedStateContainer: SerializableContainer?) :
  CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()
  private val toggleAlarmOnOffUseCase: ToggleAlarmOnOffUseCase by inject()
  private val getAlarmsInGroupFlowUseCase: GetAlarmsInGroupFlowUseCase by inject()

  private val _state: MutableStateFlow<AlarmGroupsState> =
    MutableStateFlow(
      savedStateContainer?.consume(AlarmGroupsState.serializer()) ?: AlarmGroupsState()
    )
  val state: AnyStateFlow<AlarmGroupsState> = _state.wrapToAny()

  init {
    repository
      .getAllAlarmGroupsFlow()
      .onEach { groups -> _state.update { it.copy(groups = groups) } }
      .launchIn(coroutineScope)

    state
      .map { it.expandedGroupId }
      .distinctUntilChanged()
      .flatMapLatest {
        if (it == null) {
          flowOf(emptyList())
        } else {
          getAlarmsInGroupFlowUseCase(it)
        }
      }
      .onEach { alarms -> _state.update { it.copy(expandedGroupAlarms = alarms) } }
      .launchIn(coroutineScope)
  }

  fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    coroutineScope.launch { toggleAlarmOnOffUseCase(alarm.id) }
  }

  fun onToggleGroupOnOff(group: AlarmGroupModel) {}

  fun onExpandGroup(group: AlarmGroupModel) {
    _state.update { it.copy(expandedGroupId = group.id, expandedGroupAlarms = emptyList()) }
  }

  fun onCollapseGroup() {
    _state.update { it.copy(expandedGroupId = null, expandedGroupAlarms = emptyList()) }
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = _state.value, strategy = AlarmGroupsState.serializer())
}
