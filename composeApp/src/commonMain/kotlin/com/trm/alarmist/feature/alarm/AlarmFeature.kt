package com.trm.alarmist.feature.alarm

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.AlarmsRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmFeature : CoroutineFeature(), KoinComponent {
  private val repository: AlarmsRepository by inject()

  fun addAlarm(fireAt: LocalTime, name: String?) {
    coroutineScope.launch { repository.addOneShotAlarm(fireAt, name) }
  }
}
