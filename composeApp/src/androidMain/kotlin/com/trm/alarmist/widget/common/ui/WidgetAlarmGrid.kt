package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.fillMaxSize
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.widget.common.util.deepLinkAction
import com.trm.alarmist.widget.common.util.editAlarmDeeplinkUri

@Composable
fun WidgetAlarmGrid(alarms: List<UpcomingAlarmListModel>, getGroup: (Long) -> AlarmGroupModel?) {
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
      onClick = deepLinkAction(LocalContext.current.editAlarmDeeplinkUri(item.id)),
      modifier = GlanceModifier.fillMaxSize(),
    )
  }
}
