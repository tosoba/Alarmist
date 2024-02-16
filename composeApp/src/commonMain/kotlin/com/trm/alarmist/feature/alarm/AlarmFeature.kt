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
import kotlinx.datetime.LocalDate
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
    coroutineScope.launch {
      repository.addAlarm(
        fireAt = state.fireAt,
        name = state.name,
        isOn = true,
        scheduledOnDaysOfWeek = state.scheduledOnDaysOfWeek,
        scheduledOnDates = state.scheduledOnDates,
        offOnDates = state.offOnDates,
      )
    }

  fun onFireAtChange(fireAt: LocalTime) {
    state = state.copy(fireAt = fireAt)
  }

  fun onDayOfWeekClick(dayOfWeek: DayOfWeek) {
    state =
      state.copy(
        scheduledOnDaysOfWeek =
          if (state.scheduledOnDaysOfWeek.contains(dayOfWeek)) {
            state.scheduledOnDaysOfWeek - dayOfWeek
          } else {
            state.scheduledOnDaysOfWeek + dayOfWeek
          },
        scheduledOnDates =
          if (state.scheduledOnDaysOfWeek.contains(dayOfWeek)) {
            state.scheduledOnDates
          } else {
            state.scheduledOnDates.filterNot { it.dayOfWeek == dayOfWeek }.toSet()
          },
        offOnDates = state.offOnDates.filterNot { it.dayOfWeek == dayOfWeek }.toSet(),
      )
  }

  fun onDateOnOffSwitchCheckedChange(isOn: Boolean, date: LocalDate) {
    state = state.copy(offOnDates = if (isOn) state.offOnDates - date else state.offOnDates + date)
  }

  fun onDeleteOnAllDaysWeekClick(dayOfWeek: DayOfWeek) {
    state =
      state.copy(
        scheduledOnDaysOfWeek = state.scheduledOnDaysOfWeek - dayOfWeek,
        offOnDates = state.offOnDates.filterNot { it.dayOfWeek == dayOfWeek }.toSet(),
      )
  }

  fun onDeleteOnDateClick(date: LocalDate) {
    state =
      state.copy(
        scheduledOnDates = state.scheduledOnDates - date,
        offOnDates = state.offOnDates - date,
      )
  }

  fun onScheduleOnDateClick(date: LocalDate) {
    state = state.copy(scheduledOnDates = state.scheduledOnDates + date)
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(value = state, strategy = AlarmState.serializer())
}
