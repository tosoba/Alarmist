package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.action.Action
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel

@Composable
fun WidgetAlarmListContent(
  alarms: List<WidgetAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  onCheckedChangeAction: (WidgetAlarmListModel) -> Action,
) {
  when (WidgetLayoutSize.fromLocalSize()) {
    WidgetLayoutSize.Small -> {
      WidgetAlarmList(
        alarms = alarms,
        getGroup = getGroup,
        displayHeaderSupporting = false,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
    WidgetLayoutSize.Medium -> {
      WidgetAlarmList(
        alarms = alarms,
        getGroup = getGroup,
        displayHeaderSupporting = true,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
    WidgetLayoutSize.Large -> {
      WidgetAlarmGrid(
        alarms = alarms,
        getGroup = getGroup,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
  }
}
