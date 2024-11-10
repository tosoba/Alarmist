package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.layout.fillMaxSize
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.WidgetAlarmListModel
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.emptyActionIfPreviewOrElse

@Composable
fun WidgetAlarmGrid(
  alarms: List<WidgetAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  onCheckedChangeAction: (WidgetAlarmListModel) -> Action,
) {
  WidgetLazyVerticalGrid(
    gridCells = WidgetDimensions.NUM_GRID_CELLS,
    items = alarms,
    modifier = GlanceModifier.fillMaxSize(),
    cellSpacing = WidgetDimensions.verticalItemSpacing,
  ) { item ->
    WidgetAlarmListItem(
      item = item,
      group = item.groupId?.let(getGroup),
      displayHeaderSupporting = true,
      onClick =
        emptyActionIfPreviewOrElse { actionStartMainActivity(RootStartMode.EditAlarm(item.id)) },
      onCheckedChange = emptyActionIfPreviewOrElse { onCheckedChangeAction(item) },
    )
  }
}
