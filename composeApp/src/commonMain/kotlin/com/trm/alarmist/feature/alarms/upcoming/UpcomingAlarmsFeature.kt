package com.trm.alarmist.feature.alarms.upcoming

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledOnDateFlowUseCase
import com.trm.alarmist.core.domain.usecase.GetScheduledAlarmCountsForDateRangeUseCase
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import com.trm.alarmist.core.domain.usecase.TurnAlarmOffOnDateUseCase
import com.trm.alarmist.core.domain.usecase.TurnAlarmOnOnDateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
  private val repository: AlarmRepository by inject()

  private val getAlarmsScheduledOnDateFlowUseCase: GetAlarmsScheduledOnDateFlowUseCase by inject()
  private val getScheduledAlarmCountsForDateRangeUseCase:
    GetScheduledAlarmCountsForDateRangeUseCase by
    inject()

  private val toggleAlarmOnOffUseCase: ToggleAlarmOnOffUseCase by inject()
  private val turnAlarmOnOnDateUseCase: TurnAlarmOnOnDateUseCase by inject()
  private val turnAlarmOffOnDateUseCase: TurnAlarmOffOnDateUseCase by inject()

  val groups: StateFlow<Map<Long, AlarmGroupModel>> =
    repository
      .getAllAlarmGroupsFlow()
      .map { it.associateBy(AlarmGroupModel::id) }
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyMap(),
      )

  var calendarState: UpcomingAlarmsCalendarState =
    savedStateContainer?.consume(strategy = UpcomingAlarmsCalendarState.serializer())
      ?: with(LocalDate.now()) { UpcomingAlarmsCalendarState(this, month, year) }
    private set

  private val selectedDateFlow = MutableSharedFlow<LocalDate?>()

  val selectedDateAlarmsFlow: StateFlow<List<UpcomingAlarmListModel>> =
    selectedDateFlow
      .onEach { calendarState = calendarState.copy(selectedDate = it) }
      .onStart { emit(calendarState.selectedDate) }
      .distinctUntilChanged()
      .flatMapLatest {
        if (it == null) flowOf(emptyList()) else getAlarmsScheduledOnDateFlowUseCase(it)
      }
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList(),
      )

  private val monthlyDateRangeFlow = MutableSharedFlow<ClosedRange<LocalDate>>()

  val scheduledAlarmCountsFlow: StateFlow<Map<LocalDate, Int>> =
    monthlyDateRangeFlow
      .onEach {
        val middleDate =
          LocalDate.fromEpochDays(
            it.start.toEpochDays() + (it.endInclusive.toEpochDays() - it.start.toEpochDays()) / 2
          )
        calendarState =
          calendarState.copy(currentMonth = middleDate.month, currentYear = middleDate.year)
      }
      .distinctUntilChanged()
      .flatMapLatest(getScheduledAlarmCountsForDateRangeUseCase::invoke)
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyMap(),
      )

  fun onSelectedDateChange(date: LocalDate?) {
    coroutineScope.launch { selectedDateFlow.emit(date) }
  }

  fun onMonthlyDateRangeChange(range: ClosedRange<LocalDate>) {
    coroutineScope.launch { monthlyDateRangeFlow.emit(range) }
  }

  fun onTurnAlarmOff(alarm: UpcomingAlarmListModel) {
    coroutineScope.launch { toggleAlarmOnOffUseCase(alarm.id) }
  }

  fun onTurnAlarmOffOnSelectedDate(alarm: UpcomingAlarmListModel) {
    calendarState.selectedDate?.let {
      coroutineScope.launch { turnAlarmOffOnDateUseCase(alarm.id, it) }
    }
  }

  fun onTurnAlarmOnOnSelectedDate(alarm: UpcomingAlarmListModel) {
    calendarState.selectedDate?.let {
      coroutineScope.launch { turnAlarmOnOnDateUseCase(alarm.id, it) }
    }
  }

  fun saveState(): SerializableContainer =
    SerializableContainer(
      value = calendarState,
      strategy = UpcomingAlarmsCalendarState.serializer(),
    )
}
