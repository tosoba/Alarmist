package com.trm.alarmist.core.domain.model

import kotlinx.serialization.Serializable

@Serializable data class AlarmGroupModel(val id: Long, val name: String, val color: Long)
