package com.trm.alarmist.feature.alarm

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.common.util.wrapToAny
import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmFeature(
  savedStateContainer: SerializableContainer?,
  private val mode: AlarmComponent.Mode,
) : CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()

  private val _state =
    MutableStateFlow(
      when (mode) {
        AlarmComponent.Mode.Add -> AlarmState()
        is AlarmComponent.Mode.Edit -> AlarmState(fireAtTime = null)
      }
    )
  val state: AnyStateFlow<AlarmState> = _state.wrapToAny()

  init {
    val savedState = savedStateContainer?.consume(strategy = AlarmState.serializer())
    if (savedState != null) {
      _state.value = savedState
    } else if (mode is AlarmComponent.Mode.Edit) {
      coroutineScope.launch { _state.value = AlarmState(repository.getAlarmById(mode.id)) }
    }
  }

  fun onConfirmClick(): Job =
    coroutineScope.launch {
      with(_state.value) {
        when (mode) {
          AlarmComponent.Mode.Add -> {
            repository.addAlarm(
              fireAtTime = requireNotNull(fireAtTime),
              name = name,
              isOn = true,
              scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
              scheduledOnDates = scheduledOnDates,
              offOnDates = offOnDates,
            )
          }
          is AlarmComponent.Mode.Edit -> {
            repository.editAlarm(
              id = mode.id,
              fireAtTime = requireNotNull(fireAtTime),
              name = name,
              isOn = true,
              scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
              scheduledOnDates = scheduledOnDates,
              offOnDates = offOnDates,
            )
          }
        }
      }
    }

  fun onFireAtChange(fireAtTime: LocalTime) {
    _state.update { it.copy(fireAtTime = fireAtTime) }
  }

  fun onDayOfWeekClick(dayOfWeek: DayOfWeek) {
    _state.update {
      it.copy(
        scheduledOnDaysOfWeek =
          if (it.scheduledOnDaysOfWeek.contains(dayOfWeek)) {
            it.scheduledOnDaysOfWeek - dayOfWeek
          } else {
            it.scheduledOnDaysOfWeek + dayOfWeek
          },
        scheduledOnDates =
          if (it.scheduledOnDaysOfWeek.contains(dayOfWeek)) {
            it.scheduledOnDates
          } else {
            it.scheduledOnDates.filterNot { date -> date.dayOfWeek == dayOfWeek }.toSet()
          },
        offOnDates = it.offOnDates.filterNot { date -> date.dayOfWeek == dayOfWeek }.toSet(),
      )
    }
  }

  fun onDateOnOffSwitchCheckedChange(isOn: Boolean, date: LocalDate) {
    _state.update { it.copy(offOnDates = if (isOn) it.offOnDates - date else it.offOnDates + date) }
  }

  fun onDeleteOnAllDaysWeekClick(dayOfWeek: DayOfWeek) {
    _state.update {
      it.copy(
        scheduledOnDaysOfWeek = it.scheduledOnDaysOfWeek - dayOfWeek,
        offOnDates = it.offOnDates.filterNot { date -> date.dayOfWeek == dayOfWeek }.toSet(),
      )
    }
  }

  fun onDeleteOnDateClick(date: LocalDate) {
    _state.update {
      it.copy(scheduledOnDates = it.scheduledOnDates - date, offOnDates = it.offOnDates - date)
    }
  }

  fun onScheduleOnDateClick(date: LocalDate) {
    _state.update { it.copy(scheduledOnDates = it.scheduledOnDates + date) }
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = _state.value, strategy = AlarmState.serializer())
}
