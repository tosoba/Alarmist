package com.trm.alarmist.feature.group

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.common.util.wrapToAny
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetGroupedAlarmsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GroupFeature(
  savedStateContainer: SerializableContainer?,
  private val mode: GroupComponent.Mode,
) : CoroutineFeature(), KoinComponent {
  private val getGroupedAlarmsUseCase: GetGroupedAlarmsUseCase by inject()

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

  fun onNameChange(name: String) {
    _state.update { it.copy(name = name.ifBlank { "" }) }
  }

  fun onColorChange(color: Color) {
    _state.update { it.copy(color = color.toArgb()) }
  }

  fun onAlarmSelected(alarm: AlarmListModel) {
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

  fun onConfirmClick(): Job {
    return Job()
  }
}
