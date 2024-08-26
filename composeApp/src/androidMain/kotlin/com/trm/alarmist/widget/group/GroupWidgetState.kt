package com.trm.alarmist.widget.group

import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel

internal sealed interface GroupWidgetState {
  data object NoGroupSet : GroupWidgetState

  data object Uninitialized : GroupWidgetState

  data class Initialized(val alarms: List<WidgetAlarmListModel>, val group: AlarmGroupModel) :
    GroupWidgetState
}
