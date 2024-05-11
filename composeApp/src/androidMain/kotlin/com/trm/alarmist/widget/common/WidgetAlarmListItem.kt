package com.trm.alarmist.widget.common

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.Switch
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.toFormattedString
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.widget.common.util.mediumFontSize

@Composable
internal fun WidgetAlarmListItem(alarm: AlarmListModel, modifier: GlanceModifier = GlanceModifier) {
  Column(modifier) {
    // TODO: label if exists
    Row(verticalAlignment = Alignment.CenterVertically, modifier = GlanceModifier.fillMaxWidth()) {
      Text(
        text =
          """${alarm.nextFireAtTime.toFormattedString { DateFormat.is24HourFormat(LocalContext.current) }} ${alarm.nextFireAtTime.amPmString { DateFormat.is24HourFormat(LocalContext.current) }}"""
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
