package com.trm.alarmist.feature.alarms.groups

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsInGroupFlowUseCase
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import com.trm.alarmist.core.domain.usecase.UpdateGroupOnOffUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmGroupsFeature(savedStateContainer: SerializableContainer?) :
  CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()
  private val getAlarmsInGroupFlowUseCase: GetAlarmsInGroupFlowUseCase by inject()
  private val toggleAlarmOnOffUseCase: ToggleAlarmOnOffUseCase by inject()
  private val updateGroupOnOffUseCase: UpdateGroupOnOffUseCase by inject()

  private val _state: MutableStateFlow<AlarmGroupsState> =
    MutableStateFlow(
      savedStateContainer?.consume(AlarmGroupsState.serializer()) ?: AlarmGroupsState()
    )
  val state: StateFlow<AlarmGroupsState> = _state.asStateFlow()

  init {
    repository
      .getAllAlarmGroupsFlow()
      .onEach { groups -> _state.update { it.copy(groups = groups) } }
      .launchIn(coroutineScope)

    state
      .mapNotNull { it.expandedGroupId }
      .flatMapLatest(getAlarmsInGroupFlowUseCase::invoke)
      .onEach { alarms -> _state.update { it.copy(expandedGroupAlarms = alarms) } }
      .launchIn(coroutineScope)
  }

  fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    coroutineScope.launch { toggleAlarmOnOffUseCase(alarm.id) }
  }

  fun onToggleGroupOnOff(group: AlarmGroupModel) {
    coroutineScope.launch { updateGroupOnOffUseCase(id = group.id, isOn = !group.isOn) }
  }

  fun onExpandGroup(group: AlarmGroupModel) {
    _state.update { it.copy(expandedGroupId = group.id, expandedGroupAlarms = emptyList()) }
  }

  fun onCollapseGroup() {
    _state.update { it.copy(expandedGroupId = null, expandedGroupAlarms = emptyList()) }
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = _state.value, strategy = AlarmGroupsState.serializer())
}
