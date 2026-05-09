package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.action.Action
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.util.LocalWidgetLayoutType
import com.trm.alarmist.widget.common.util.LocalWidgetMode
import com.trm.alarmist.widget.common.util.WidgetMode

@Composable
fun WidgetAlarmListContent(
  alarms: List<WidgetAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  onCheckedChangeAction: (WidgetAlarmListModel) -> Action,
) {
  if (LocalWidgetMode.current == WidgetMode.NO_LAZY_LAYOUTS_PREVIEW) {
    WidgetAlarmListPreview(
      alarms = alarms,
      getGroup = getGroup,
      displayHeaderSupporting = false,
      onCheckedChangeAction = onCheckedChangeAction,
    )
  } else {
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
}
