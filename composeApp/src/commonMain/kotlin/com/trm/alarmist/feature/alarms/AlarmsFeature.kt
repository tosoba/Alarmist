package com.trm.alarmist.feature.alarms

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.AlarmsRepository

class AlarmsFeature(private val repository: AlarmsRepository) : CoroutineFeature() {}
