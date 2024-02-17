package com.trm.alarmist.feature.alarms.list

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.common.util.wrapToAny
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListFeature : CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()

  private val _alarms = MutableStateFlow<List<AlarmListModel>>(emptyList())
  val alarms: AnyStateFlow<List<AlarmListModel>> = _alarms.wrapToAny()

  init {
    repository.getAllAlarmsListFlow().onEach { _alarms.value = it }.launchIn(coroutineScope)
  }

  fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    coroutineScope.launch { repository.toggleAlarmOnOff(alarm.id) }
  }
}
