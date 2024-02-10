package com.trm.alarmist.feature.alarm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmFeature(savedState: SerializableContainer?, mode: AlarmComponent.Mode) :
  CoroutineFeature(), KoinComponent {
  var state: AlarmState by
    mutableStateOf(savedState?.consume(strategy = AlarmState.serializer()) ?: AlarmState())
    private set

  private val repository: AlarmRepository by inject()

  fun onConfirmClick(): Job =
    coroutineScope.launch { repository.addOneShotAlarm(state.fireAt, state.name) }

  fun onFireAtChange(fireAt: LocalTime) {
    state = state.copy(fireAt = fireAt)
  }

  fun onDayOfWeekClick(dayOfWeek: DayOfWeek) {
    state =
      state.copy(
        selectedDaysOfWeek =
          if (state.selectedDaysOfWeek.contains(dayOfWeek)) {
            state.selectedDaysOfWeek - dayOfWeek
          } else {
            state.selectedDaysOfWeek + dayOfWeek
          }
      )
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = state, strategy = AlarmState.serializer())
}
