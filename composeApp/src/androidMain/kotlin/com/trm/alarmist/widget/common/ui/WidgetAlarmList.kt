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
fun WidgetAlarmList(
  alarms: List<UpcomingAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  displayHeaderSupporting: Boolean,
) {
  WidgetLazyColumn(
    items = alarms,
    modifier = GlanceModifier.fillMaxSize(),
    verticalItemsSpacing = WidgetDimensions.verticalItemSpacing,
  ) { item ->
    WidgetAlarmListItem(
      item = item,
      group = item.groupId?.let(getGroup),
      displayHeaderSupporting = displayHeaderSupporting,
      onClick = deepLinkAction(LocalContext.current.editAlarmDeeplinkUri(item.id)),
      modifier = GlanceModifier.fillMaxSize(),
    )
  }
}
