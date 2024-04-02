package com.trm.alarmist.feature.alarm

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.model.AnyStateFlow
import com.trm.alarmist.core.common.model.wrapToAny
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.usecase.AddAlarmUseCase
import com.trm.alarmist.core.domain.usecase.EditAlarmUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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
  private val addAlarmUseCase: AddAlarmUseCase by inject()
  private val editAlarmUseCase: EditAlarmUseCase by inject()

  private val _state: MutableStateFlow<AlarmState> =
    MutableStateFlow(
      when (mode) {
        AlarmComponent.Mode.Add -> AlarmState()
        is AlarmComponent.Mode.Edit -> AlarmState(mode.alarm)
      }
    )
  val state: AnyStateFlow<AlarmState> = _state.wrapToAny()

  val groups: AnyStateFlow<List<AlarmGroupModel>> =
    repository
      .getAllAlarmGroupsFlow()
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList(),
      )
      .wrapToAny()

  init {
    val savedState = savedStateContainer?.consume(strategy = AlarmState.serializer())
    if (savedState != null) {
      _state.value = savedState
    } else if (mode is AlarmComponent.Mode.Edit) {
      coroutineScope.launch { _state.value = AlarmState(repository.getAlarmById(mode.alarm.id)) }
    }
  }

  fun onConfirmClick(): Job =
    coroutineScope.launch {
      with(_state.value) {
        when (mode) {
          AlarmComponent.Mode.Add -> {
            addAlarmUseCase(
              groupId = groupId,
              name = name,
              fireAtTime = fireAtTime,
              isOn = true,
              scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
              scheduledOnDates = scheduledOnDates,
              offOnDates = offOnDates,
            )
          }
          is AlarmComponent.Mode.Edit -> {
            editAlarmUseCase(
              id = mode.alarm.id,
              groupId = groupId,
              fireAtTime = fireAtTime,
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

  fun onNameChange(name: String) {
    _state.update { it.copy(name = name.ifBlank { null }) }
  }

  fun onFireAtChange(fireAtTime: LocalTime) {
    _state.update { it.copy(fireAtTime = fireAtTime) }
  }

  fun onDayOfWeekClick(dayOfWeek: DayOfWeek) {
    _state.update {
      it.copy(
        scheduledOnDaysOfWeek =
          if (dayOfWeek in it.scheduledOnDaysOfWeek) {
            it.scheduledOnDaysOfWeek - dayOfWeek
          } else {
            it.scheduledOnDaysOfWeek + dayOfWeek
          },
        scheduledOnDates =
          if (dayOfWeek in it.scheduledOnDaysOfWeek) {
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

  fun onGroupClick(group: AlarmGroupModel) {
    _state.update { it.copy(groupId = group.id) }
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = _state.value, strategy = AlarmState.serializer())
}
