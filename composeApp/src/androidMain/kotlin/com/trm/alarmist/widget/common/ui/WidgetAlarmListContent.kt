package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel

@Composable
fun WidgetAlarmListContent(
  alarms: List<UpcomingAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
) {
  when (WidgetLayoutSize.fromLocalSize()) {
    WidgetLayoutSize.Small -> {
      WidgetAlarmList(alarms = alarms, getGroup = getGroup, displayHeaderSupporting = false)
    }
    WidgetLayoutSize.Medium -> {
      WidgetAlarmList(alarms = alarms, getGroup = getGroup, displayHeaderSupporting = true)
    }
    WidgetLayoutSize.Large -> {
      WidgetAlarmGrid(alarms = alarms, getGroup = getGroup)
    }
  }
}
