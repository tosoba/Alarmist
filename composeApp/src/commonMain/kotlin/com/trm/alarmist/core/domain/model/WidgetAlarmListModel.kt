package com.trm.alarmist.core.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

data class WidgetAlarmListModel(
  val id: Long,
  val groupId: Long?,
  val fireAtTime: LocalTime,
  val name: String?,
  val isOn: Boolean,
  val fireOnDateTime: LocalDateTime?,
  val isCustomScheduled: Boolean,
) {
  constructor(
    alarm: UpcomingAlarmListModel
  ) : this(
    id = alarm.id,
    groupId = alarm.groupId,
    fireAtTime = alarm.fireAtTime,
    name = alarm.name,
    isOn = alarm.status == UpcomingAlarmListStatus.ON,
    fireOnDateTime = alarm.fireOnDateTime,
    isCustomScheduled = alarm.scheduledOnDaysOfWeek.isNotEmpty() || alarm.date != null,
  )

  constructor(
    alarm: AlarmListModel
  ) : this(
    id = alarm.id,
    groupId = alarm.groupId,
    fireAtTime = alarm.fireAtTime,
    name = alarm.name,
    isOn = alarm.isOn,
    fireOnDateTime = alarm.fireOnDateTime,
    isCustomScheduled =
      alarm.scheduledOnDaysOfWeek.isNotEmpty() || alarm.closestScheduledOnDate != null,
  )
}
