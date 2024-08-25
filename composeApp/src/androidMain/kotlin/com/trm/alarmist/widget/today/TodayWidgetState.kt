package com.trm.alarmist.widget.today

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel

internal data class TodayWidgetState(
  val alarms: List<WidgetAlarmListModel>,
  val groups: Map<Long, AlarmGroupModel>,
)
