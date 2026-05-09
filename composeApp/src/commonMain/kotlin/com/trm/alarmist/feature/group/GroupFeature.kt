package com.trm.alarmist.feature.group

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.AddGroupUseCase
import com.trm.alarmist.core.domain.usecase.DeleteGroupUseCase
import com.trm.alarmist.core.domain.usecase.EditGroupUseCase
import com.trm.alarmist.core.domain.usecase.GetGroupedAlarmsFlowUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GroupFeature(
  savedStateContainer: SerializableContainer?,
  private val mode: GroupComponent.Mode,
) : CoroutineFeature(), KoinComponent {
  private val getGroupedAlarmsFlowUseCase: GetGroupedAlarmsFlowUseCase by inject()
  private val addGroupUseCase: AddGroupUseCase by inject()
  private val editGroupUseCase: EditGroupUseCase by inject()
  private val deleteGroupUseCase: DeleteGroupUseCase by inject()

  private val _state: MutableStateFlow<GroupState> =
    MutableStateFlow(
      savedStateContainer?.consume(strategy = GroupState.serializer())
        ?: when (mode) {
          GroupComponent.Mode.Add -> GroupState()
          is GroupComponent.Mode.Edit -> GroupState(mode.group)
        }
    )
  val state: StateFlow<GroupState> = _state.asStateFlow()

  init {
    getGroupedAlarmsFlowUseCase()
      .onEach { (alarms, groups) -> _state.update { it.copy(alarms = alarms, groups = groups) } }
      .launchIn(coroutineScope)
  }

  fun onDeleteClick(): Job = coroutineScope.launch {
    check(mode is GroupComponent.Mode.Edit)
    deleteGroupUseCase(mode.group.id)
  }

  fun onConfirmClick(): Job? =
    with(_state.value) {
      if (name.isBlank()) {
        _state.update { it.copy(blankNameError = true) }
        return null
      }

      coroutineScope.launch {
        when (mode) {
          GroupComponent.Mode.Add -> {
            addGroupUseCase(name = name, color = color, alarmIds = selectedAlarmIds)
          }
          is GroupComponent.Mode.Edit -> {
            editGroupUseCase(
              id = mode.group.id,
              name = name,
              color = color,
              alarmIds = selectedAlarmIds,
            )
          }
        }
      }
    }

  fun onNameChange(name: String) {
    _state.update { it.copy(name = name.ifBlank { "" }, blankNameError = name.isBlank()) }
  }

  fun onColorChange(color: Color) {
    _state.update { it.copy(color = color.toArgb()) }
  }

  fun onToggleAlarmSelection(alarm: AlarmListModel) {
    _state.update {
      it.copy(
        selectedAlarmIds =
          if (alarm.id in it.selectedAlarmIds) it.selectedAlarmIds - alarm.id
          else it.selectedAlarmIds + alarm.id
      )
    }
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = _state.value, strategy = GroupState.serializer())
}
