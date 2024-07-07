package com.trm.alarmist.feature.widget.config.group

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsInGroupFlowUseCase
import com.trm.alarmist.core.system.WidgetManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class GroupWidgetConfigFeature(savedStateContainer: SerializableContainer?) :
  CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()
  private val getAlarmsInGroupFlowUseCase: GetAlarmsInGroupFlowUseCase by inject()

  private val widgetManager: WidgetManager by inject()

  private val _state: MutableStateFlow<GroupWidgetConfigState> =
    MutableStateFlow(
      savedStateContainer?.consume(GroupWidgetConfigState.serializer()) ?: GroupWidgetConfigState()
    )
  val state: StateFlow<GroupWidgetConfigState> = _state.asStateFlow()

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

  fun onChooseGroup(group: AlarmGroupModel) {
    _state.update { it.copy(chosenGroupId = if (group.id == it.chosenGroupId) null else group.id) }
  }

  fun onExpandGroup(group: AlarmGroupModel) {
    _state.update { it.copy(expandedGroupId = group.id, expandedGroupAlarms = emptyList()) }
  }

  fun onCollapseGroup() {
    _state.update { it.copy(expandedGroupId = null, expandedGroupAlarms = emptyList()) }
  }

  fun onConfirmClick(widgetId: Int) {
    state.value.chosenGroupId?.let { groupId ->
      widgetManager.updateWidgetGroup(widgetId = widgetId, groupId = groupId)
    }
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = _state.value, strategy = GroupWidgetConfigState.serializer())
}
