package com.trm.alarmist.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AlarmGroupModel(val id: Long, val name: String, val color: Long, val alarmsCount: Long) {
  companion object {
    const val UNGROUPED_ID = -1L
    const val UNGROUPED_NAME = "Ungrouped"
    const val TRANSPARENT_COLOR = 0L
  }
}
