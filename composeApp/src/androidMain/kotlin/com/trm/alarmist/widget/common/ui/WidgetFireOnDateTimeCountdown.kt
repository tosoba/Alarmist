package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.LocalContext
import androidx.glance.text.Text
import com.trm.alarmist.R
import com.trm.alarmist.core.common.util.now
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun WidgetFireOnDateTimeCountdown(fireOnDateTime: LocalDateTime) {
  val durationInWholeDays =
    (fireOnDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds() -
        now().toEpochMilliseconds())
      .toDuration(DurationUnit.MILLISECONDS)
      .inWholeDays
  when {
    durationInWholeDays > 1L -> {
      Text(text = "$durationInWholeDays days", style = WidgetTextStyles.supportingText)
    }
    durationInWholeDays == 1L -> {
      Text(text = "$durationInWholeDays day", style = WidgetTextStyles.supportingText)
    }
    else -> {
      val color = WidgetTextStyles.supportingText.color.getColor(LocalContext.current).toArgb()
      WidgetChronometerCountdown(dateTimeTo = fireOnDateTime) {
        setTextColor(R.id.widget_chronometer, color)
      }
    }
  }
}
