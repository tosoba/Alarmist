package com.trm.alarmist.core.domain.model

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class EditAlarmInputModel(
  val id: Long,
  val groupId: Long?,
  val fireAtTime: LocalTime,
  val name: String?,
) {
  constructor(alarm: AlarmListModel) : this(alarm.id, alarm.groupId, alarm.fireAtTime, alarm.name)

  constructor(
    alarm: UpcomingAlarmListModel
  ) : this(alarm.id, alarm.groupId, alarm.fireAtTime, alarm.name)
}
