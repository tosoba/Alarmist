package com.trm.alarmist.feature.alarms.list

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.common.util.wrapToAny
import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListFeature : CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()

  private val _alarms = MutableStateFlow<List<AlarmListItem>>(emptyList())
  val alarms: AnyStateFlow<List<AlarmListItem>> = _alarms.wrapToAny()

  init {
    repository
      .getAllAlarms()
      .onEach { _alarms.value = it.map { alarm -> AlarmListItem(alarm.fireAt.time, alarm.name) } }
      .launchIn(coroutineScope)
  }
}
