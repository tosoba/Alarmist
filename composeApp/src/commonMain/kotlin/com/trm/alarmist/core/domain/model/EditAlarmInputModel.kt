package com.trm.alarmist.core.domain.model

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class EditAlarmInputModel(
  val id: Long,
  val groupId: Long?,
  val fireAtTime: LocalTime,
  val name: String?,
  val isOn: Boolean,
) {
  constructor(
    alarm: AlarmListModel
  ) : this(
    id = alarm.id,
    groupId = alarm.groupId,
    fireAtTime = alarm.fireAtTime,
    name = alarm.name,
    isOn = alarm.isOn,
  )

  constructor(
    alarm: UpcomingAlarmListModel
  ) : this(
    id = alarm.id,
    groupId = alarm.groupId,
    fireAtTime = alarm.fireAtTime,
    name = alarm.name,
    isOn = alarm.status != UpcomingAlarmListStatus.OFF,
  )
}
