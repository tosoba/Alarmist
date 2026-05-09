package com.trm.alarmist.app.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.WidgetAlarmListModel
import com.trm.alarmist.feature.root.RootStartMode
import com.trm.alarmist.app.widget.common.util.actionStartMainActivity
import com.trm.alarmist.app.widget.common.util.emptyActionIfPreviewOrElse

@Composable
fun WidgetAlarmList(
  alarms: List<WidgetAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  displayHeaderSupporting: Boolean,
  onCheckedChangeAction: (WidgetAlarmListModel) -> Action,
) {
  WidgetLazyColumn(
    items = alarms,
    modifier = GlanceModifier.fillMaxSize().cornerRadius(16.dp),
    verticalItemsSpacing = WidgetDimensions.verticalItemSpacing,
  ) { item ->
    WidgetAlarmListItem(
      item = item,
      group = item.groupId?.let(getGroup),
      displayHeaderSupporting = displayHeaderSupporting,
      onClick =
        emptyActionIfPreviewOrElse { actionStartMainActivity(RootStartMode.EditAlarm(item.id)) },
      onCheckedChange = emptyActionIfPreviewOrElse { onCheckedChangeAction(item) },
    )
  }
}

@Composable
fun WidgetAlarmListPreview(
  alarms: List<WidgetAlarmListModel>,
  getGroup: (Long) -> AlarmGroupModel?,
  displayHeaderSupporting: Boolean,
  onCheckedChangeAction: (WidgetAlarmListModel) -> Action,
) {
  Column(modifier = GlanceModifier.fillMaxWidth().height(125.dp)) {
    alarms.forEachIndexed { index, alarm ->
      WidgetAlarmListItem(
        item = alarm,
        group = alarm.groupId?.let(getGroup),
        displayHeaderSupporting = displayHeaderSupporting,
        onClick = null,
        onCheckedChange = emptyActionIfPreviewOrElse { onCheckedChangeAction(alarm) },
      )

      if (index != alarms.lastIndex) {
        Spacer(modifier = GlanceModifier.height(WidgetDimensions.verticalItemSpacing))
      }
    }
  }
}
