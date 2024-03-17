package com.trm.alarmist.feature.alarms.upcoming

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledOnDateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class UpcomingAlarmsFeature(savedStateContainer: SerializableContainer?) :
  CoroutineFeature(), KoinComponent {
  private val getAlarmsScheduledOnDateUseCase: GetAlarmsScheduledOnDateUseCase by inject()

  var calendarState: UpcomingAlarmsCalendarState =
    savedStateContainer?.consume(strategy = UpcomingAlarmsCalendarState.serializer())
      ?: with(LocalDate.now()) { UpcomingAlarmsCalendarState(null, month, year) }
    private set

  private val selectedDateFlow = MutableSharedFlow<LocalDate?>()

  val selectedDateAlarmsFlow: StateFlow<List<AlarmListModel>> =
    selectedDateFlow
      .onEach { calendarState = calendarState.copy(selectedDate = it) }
      .onStart { emit(calendarState.selectedDate) }
      .distinctUntilChanged()
      .flatMapLatest {
        if (it == null) flowOf(emptyList()) else getAlarmsScheduledOnDateUseCase(it)
      }
      .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

  fun onSelectedDateChange(date: LocalDate?) {
    coroutineScope.launch { selectedDateFlow.emit(date) }
  }

  fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    // TODO: pause/play alarm on given date or globally turn it on/off?
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(
      value = calendarState,
      strategy = UpcomingAlarmsCalendarState.serializer(),
    )
}
