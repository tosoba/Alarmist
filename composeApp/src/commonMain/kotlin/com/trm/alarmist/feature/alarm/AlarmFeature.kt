package com.trm.alarmist.feature.alarm

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.domain.AlarmsRepository

class AlarmFeature(private val repository: AlarmsRepository) : CoroutineFeature() {}
