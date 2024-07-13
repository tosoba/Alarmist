package com.trm.alarmist.widget.common.model

import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListStatus
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
    model: UpcomingAlarmListModel
  ) : this(
    id = model.id,
    groupId = model.groupId,
    fireAtTime = model.fireAtTime,
    name = model.name,
    isOn = model.status == UpcomingAlarmListStatus.ON,
    fireOnDateTime = model.fireOnDateTime,
    isCustomScheduled = model.scheduledOnDaysOfWeek.isNotEmpty() || model.date != null,
  )

  constructor(
    model: AlarmListModel
  ) : this(
    id = model.id,
    groupId = model.groupId,
    fireAtTime = model.fireAtTime,
    name = model.name,
    isOn = model.isOn,
    fireOnDateTime = model.fireOnDateTime,
    isCustomScheduled =
      model.scheduledOnDaysOfWeek.isNotEmpty() || model.closestScheduledOnDate != null,
  )
}
