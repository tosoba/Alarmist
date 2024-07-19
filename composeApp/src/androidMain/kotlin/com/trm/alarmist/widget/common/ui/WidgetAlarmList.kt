package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.layout.fillMaxSize
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.util.deepLinkAction
import com.trm.alarmist.core.common.util.editAlarmDeeplinkUri

@Composable
fun WidgetAlarmList(
  alarms: List<WidgetAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  displayHeaderSupporting: Boolean,
  onCheckedChangeAction: (WidgetAlarmListModel) -> Action,
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
      onCheckedChange = onCheckedChangeAction(item),
      modifier = GlanceModifier.fillMaxSize(),
    )
  }
}
