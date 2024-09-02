package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.action.Action
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutSize

@Composable
fun WidgetAlarmListContent(
  alarms: List<WidgetAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  onCheckedChangeAction: (WidgetAlarmListModel) -> Action,
) {
  when (LocalWidgetLayoutSize.current) {
    is WidgetLayoutSize.Small -> {
      WidgetAlarmList(
        alarms = alarms,
        getGroup = getGroup,
        displayHeaderSupporting = false,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
    is WidgetLayoutSize.Medium -> {
      WidgetAlarmList(
        alarms = alarms,
        getGroup = getGroup,
        displayHeaderSupporting = true,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
    is WidgetLayoutSize.Large -> {
      WidgetAlarmGrid(
        alarms = alarms,
        getGroup = getGroup,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
  }
}
