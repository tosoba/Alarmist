package com.trm.alarmist.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PartitionedAlarms(val oneTime: List<AlarmModel>, val scheduled: List<AlarmModel>)
