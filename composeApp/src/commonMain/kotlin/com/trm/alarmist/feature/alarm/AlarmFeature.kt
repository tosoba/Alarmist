package com.trm.alarmist.feature.alarm

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.AlarmsRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmFeature(savedState: SerializableContainer?, mode: AlarmComponent.Mode) :
  CoroutineFeature(), KoinComponent {
  var state: AlarmState = savedState?.consume(strategy = AlarmState.serializer()) ?: AlarmState()
    private set

  private val repository: AlarmsRepository by inject()

  fun onConfirmClick() {
    coroutineScope.launch { repository.addOneShotAlarm(state.fireAt, state.name) }
  }

  fun onFireAtChange(fireAt: LocalTime) {
    state = state.copy(fireAt = fireAt)
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = state, strategy = AlarmState.serializer())
}
