package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.action.Action
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType

@Composable
fun WidgetAlarmListContent(
  alarms: List<WidgetAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  onCheckedChangeAction: (WidgetAlarmListModel) -> Action,
) {
  when (LocalWidgetLayoutType.current) {
    is WidgetLayoutType.Small -> {
      WidgetAlarmList(
        alarms = alarms,
        getGroup = getGroup,
        displayHeaderSupporting = false,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
    is WidgetLayoutType.Medium -> {
      WidgetAlarmList(
        alarms = alarms,
        getGroup = getGroup,
        displayHeaderSupporting = true,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
    is WidgetLayoutType.Large -> {
      WidgetAlarmGrid(
        alarms = alarms,
        getGroup = getGroup,
        onCheckedChangeAction = onCheckedChangeAction,
      )
    }
  }
}
