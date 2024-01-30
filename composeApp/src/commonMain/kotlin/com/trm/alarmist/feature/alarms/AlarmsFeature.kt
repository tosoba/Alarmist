package com.trm.alarmist.feature.alarms

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.AlarmsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmsFeature : CoroutineFeature(), KoinComponent {
  private val repository: AlarmsRepository by inject()
}
