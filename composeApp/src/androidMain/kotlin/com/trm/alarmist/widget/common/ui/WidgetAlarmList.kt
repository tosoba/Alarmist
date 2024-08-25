package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.layout.fillMaxSize
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.util.actionStartMainActivity
import com.trm.alarmist.widget.common.util.emptyActionIfPreviewOrElse

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
      onClick =
        emptyActionIfPreviewOrElse(actionStartMainActivity(RootStartMode.EditAlarm(item.id))),
      onCheckedChange = emptyActionIfPreviewOrElse(onCheckedChangeAction(item)),
      modifier = GlanceModifier.fillMaxSize(),
    )
  }
}
