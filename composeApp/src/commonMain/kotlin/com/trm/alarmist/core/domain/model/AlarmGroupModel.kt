package com.trm.alarmist.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AlarmGroupModel(
  val id: Long,
  val name: String,
  val color: Long,
  val alarmsCount: Long,
  val isOn: Boolean,
) {
  companion object {
    const val UNGROUPED_ID = -1L
  }
}
