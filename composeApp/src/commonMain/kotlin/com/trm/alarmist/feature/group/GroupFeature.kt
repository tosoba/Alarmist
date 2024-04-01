package com.trm.alarmist.feature.group

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.model.AnyStateFlow
import com.trm.alarmist.core.common.model.wrapToAny
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetGroupedAlarmsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
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
  private val getGroupedAlarmsUseCase: GetGroupedAlarmsUseCase by inject()
  private val repository: AlarmRepository by inject()

  private val _state: MutableStateFlow<GroupState> =
    MutableStateFlow(
      savedStateContainer?.consume(strategy = GroupState.serializer())
        ?: when (mode) {
          GroupComponent.Mode.Add -> GroupState()
          is GroupComponent.Mode.Edit -> GroupState(mode.group)
        }
    )
  val state: AnyStateFlow<GroupState> = _state.wrapToAny()

  init {
    getGroupedAlarmsUseCase()
      .onEach { (alarms, groups) -> _state.update { it.copy(alarms = alarms, groups = groups) } }
      .launchIn(coroutineScope)
  }

  fun onConfirmClick(): Job =
    coroutineScope.launch {
      with(_state.value) {
        when (mode) {
          GroupComponent.Mode.Add -> {
            repository.addGroup(name = name, color = color, alarmIds = selectedAlarmIds)
          }
          is GroupComponent.Mode.Edit -> {
            repository.editGroup(
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
    _state.update { it.copy(name = name.ifBlank { "" }) }
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
