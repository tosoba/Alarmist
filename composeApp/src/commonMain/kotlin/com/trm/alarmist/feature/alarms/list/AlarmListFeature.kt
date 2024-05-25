package com.trm.alarmist.feature.alarms.list

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListFeature : CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()
  private val toggleAlarmOnOffUseCase: ToggleAlarmOnOffUseCase by inject()

  val alarms: StateFlow<Initializable<List<AlarmListModel>>> =
    repository
      .getAllAlarmsListFlow()
      .map { Initializable(data = it, initialized = true) }
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = Initializable(emptyList()),
      )

  val groups: StateFlow<Map<Long, AlarmGroupModel>> =
    repository
      .getAllAlarmGroupsFlow()
      .map { it.associateBy(AlarmGroupModel::id) }
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyMap(),
      )

  fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    coroutineScope.launch { toggleAlarmOnOffUseCase(alarm.id) }
  }
}
