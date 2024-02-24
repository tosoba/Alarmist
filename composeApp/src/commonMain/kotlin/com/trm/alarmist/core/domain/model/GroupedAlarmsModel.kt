package com.trm.alarmist.core.domain.model

data class GroupedAlarmsModel(
  val alarms: Map<Long, List<AlarmListModel>>,
  val groups: Map<Long, AlarmGroupModel>,
)
