package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.toFormattedString
import kotlinx.datetime.LocalTime

@Composable
fun WidgetAlarmFireAtTimeText(
  fireAtTime: LocalTime,
  is24HourFormat: Boolean,
  useFullFormat: Boolean,
  modifier: GlanceModifier = GlanceModifier,
  style: TextStyle = TextDefaults.defaultTextStyle,
) {
  Text(
    text =
      if (useFullFormat) {
        """${fireAtTime.toFormattedString { is24HourFormat }} ${fireAtTime.amPmString { is24HourFormat }}"""
          .trim()
      } else {
        fireAtTime.toFormattedString { is24HourFormat }
      },
    modifier = modifier,
    style = style,
    maxLines = 1,
  )
}
