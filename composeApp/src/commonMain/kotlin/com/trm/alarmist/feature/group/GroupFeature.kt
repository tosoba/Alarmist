package com.trm.alarmist.feature.group

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.common.util.wrapToAny
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class GroupFeature(
  savedStateContainer: SerializableContainer?,
  private val mode: GroupComponent.Mode,
) : CoroutineFeature() {
  private val _state =
    MutableStateFlow(
      savedStateContainer?.consume(strategy = GroupState.serializer()) ?: GroupState()
    )
  val state: AnyStateFlow<GroupState> = _state.wrapToAny()

  fun onNameChange(name: String) {
    _state.update { it.copy(name = name.ifBlank { "" }) }
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = _state.value, strategy = GroupState.serializer())

  fun onConfirmClick(): Job {
    return Job()
  }
}
