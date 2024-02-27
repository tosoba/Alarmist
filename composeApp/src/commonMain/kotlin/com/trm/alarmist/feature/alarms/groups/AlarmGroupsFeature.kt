package com.trm.alarmist.feature.alarms.groups

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.common.util.wrapToAny
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmGroupsFeature : CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()
  private val toggleAlarmOnOffUseCase: ToggleAlarmOnOffUseCase by inject()

  private val _groups = MutableStateFlow<List<AlarmGroupModel>>(emptyList())
  val groups: AnyStateFlow<List<AlarmGroupModel>> = _groups.wrapToAny()

  init {
    repository.getAllAlarmGroupsFlow().onEach { _groups.value = it }.launchIn(coroutineScope)
  }

  fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    coroutineScope.launch { toggleAlarmOnOffUseCase(alarm.id) }
  }

  fun onToggleGroupOnOff(group: AlarmGroupModel) {}
}
