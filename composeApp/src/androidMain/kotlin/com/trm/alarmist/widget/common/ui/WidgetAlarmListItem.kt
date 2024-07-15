package com.trm.alarmist.widget.common.ui

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.appwidget.Switch
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import com.trm.alarmist.R
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.ui.buildAlarmLabelText
import com.trm.alarmist.widget.common.model.WidgetAlarmListModel
import com.trm.alarmist.widget.common.util.stringResource

@Composable
fun WidgetAlarmListItem(
  item: WidgetAlarmListModel,
  group: AlarmGroupModel?,
  displayHeaderSupporting: Boolean,
  onClick: Action?,
  onCheckedChange: Action,
  modifier: GlanceModifier = GlanceModifier,
) {
  @Composable
  fun TitleText() {
    buildAlarmLabelText(item.name, group).takeIf(String::isNotEmpty)?.let {
      Text(text = it, maxLines = 1, style = WidgetTextStyles.titleText)
    }
  }

  @Composable
  fun SupportingText() {
    Text(
      text =
        stringResource(
          if (item.isCustomScheduled) R.string.custom_scheduled else R.string.one_time
        ),
      maxLines = 2,
      style = WidgetTextStyles.supportingText,
    )
  }

  @Composable
  fun Leading() {
    WidgetAlarmFireAtTimeText(
      fireAtTime = item.fireAtTime,
      is24HourFormat = DateFormat.is24HourFormat(LocalContext.current),
      useFullFormat = displayHeaderSupporting,
      style =
        WidgetTextStyles.leadingText(
          fontWeight = if (item.isOn) FontWeight.Medium else FontWeight.Normal
        ),
    )
  }

  @Composable
  fun Trailing() {
    Column {
      Switch(checked = item.isOn, onCheckedChange = onCheckedChange)
      item.fireOnDateTime?.let { WidgetFireOnDateTimeRemainingText(it) }
    }
  }

  WidgetListItem(
    modifier =
      modifier
        .padding(WidgetDimensions.fillItemItemPadding)
        .cornerRadius(WidgetDimensions.filledItemCornerRadius)
        .background(
          if (item.isOn) GlanceTheme.colors.primaryContainer
          else GlanceTheme.colors.secondaryContainer
        ),
    headlineContent = { if (displayHeaderSupporting) TitleText() },
    supportingContent = { if (displayHeaderSupporting) SupportingText() },
    onClick = onClick,
    leadingContent = { Leading() },
    trailingContent = { Trailing() },
  )
}
