package com.trm.alarmist.widget.common.ui

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.Switch
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.preview.Surfaces
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toFormattedString
import com.trm.alarmist.core.common.util.toLocalTimeDefault
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.widget.common.util.mediumFontSize
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

@Composable
internal fun WidgetAlarmListItem(
  alarm: AlarmListModel,
  modifier: GlanceModifier = GlanceModifier,
  is24HourFormat: @Composable () -> Boolean = { DateFormat.is24HourFormat(LocalContext.current) },
) {
  Column(modifier) {
    // TODO: label if exists
    Row(verticalAlignment = Alignment.CenterVertically, modifier = GlanceModifier.fillMaxWidth()) {
      Text(
        text =
          """${alarm.nextFireAtTime.toFormattedString(is24HourFormat)} ${alarm.nextFireAtTime.amPmString(is24HourFormat)}"""
            .trim(),
        maxLines = 1,
        style = TextDefaults.defaultTextStyle.copy(fontSize = mediumFontSize.sp),
      )

      Spacer(modifier = GlanceModifier.defaultWeight())

      Switch(
        checked = true,
        onCheckedChange = {
          // TODO: send broadcast to turn off alarm
        },
      )
    }
    // TODO: countdown
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Composable
@Preview(Surfaces.APP_WIDGET)
private fun WidgetAlarmListItemPreview() {
  GlanceTheme {
    WidgetAlarmListItem(
      alarm =
        AlarmListModel(
          id = 0L,
          groupId = null,
          fireAtTime = LocalTime.now(),
          name = "Test alarm",
          isOn = true,
          fireOnDateTime =
            LocalDateTime(
              date = LocalDate.now(),
              time = Clock.System.now().plus(1, DateTimeUnit.HOUR).toLocalTimeDefault(),
            ),
          scheduledOnDaysOfWeek = emptyList(),
          scheduledOnClosestDate = null,
          scheduledOnMultipleDates = false,
          snoozedFireAtTime = null,
        ),
      is24HourFormat = { true },
    )
  }
}
