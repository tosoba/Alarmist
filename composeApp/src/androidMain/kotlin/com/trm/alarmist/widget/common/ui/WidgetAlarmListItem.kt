package com.trm.alarmist.widget.common.ui

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.appwidget.Switch
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListStatus
import com.trm.alarmist.core.ui.buildAlarmLabelText
import com.trm.alarmist.widget.common.util.stringResource
import com.trm.alarmist.widget.common.util.toggleAlarmOnOffIntent
import kotlinx.datetime.LocalDate

@Composable
fun WidgetAlarmListItem(
  item: UpcomingAlarmListModel,
  group: AlarmGroupModel?,
  displayHeaderSupporting: Boolean,
  onClick: Action?,
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
          id =
            if (item.scheduledOnDaysOfWeek.isNotEmpty() || item.date != null) {
              R.string.custom_scheduled
            } else {
              R.string.one_time
            }
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
          fontWeight =
            if (item.status == UpcomingAlarmListStatus.ON) FontWeight.Medium else FontWeight.Normal
        ),
    )
  }

  @Composable
  fun Trailing() {
    Column {
      Switch(
        checked = item.status == UpcomingAlarmListStatus.ON,
        onCheckedChange =
          actionSendBroadcast(LocalContext.current.toggleAlarmOnOffIntent(item.id, LocalDate.now())),
      )

      item.fireOnDateTime?.let { WidgetFireOnDateTimeCountdown(it) }
    }
  }

  WidgetListItem(
    modifier =
      modifier
        .padding(WidgetDimensions.fillItemItemPadding)
        .cornerRadius(WidgetDimensions.filledItemCornerRadius)
        .background(
          if (item.status == UpcomingAlarmListStatus.ON) GlanceTheme.colors.primaryContainer
          else GlanceTheme.colors.secondaryContainer
        ),
    headlineContent = { if (displayHeaderSupporting) TitleText() },
    supportingContent = { if (displayHeaderSupporting) SupportingText() },
    onClick = onClick,
    leadingContent = { Leading() },
    trailingContent = { Trailing() },
  )
}
