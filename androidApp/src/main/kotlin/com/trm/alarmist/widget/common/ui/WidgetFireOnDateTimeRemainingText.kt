package com.trm.alarmist.widget.common.ui

import androidx.compose.runtime.Composable
import androidx.glance.text.Text
import com.trm.alarmist.app.R
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.widget.common.util.stringResource
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun WidgetFireOnDateTimeRemainingText(fireOnDateTime: LocalDateTime) {
  val remaining = fireOnDateTime.toInstant(TimeZone.currentSystemDefault()) - now()

  Text(
    text =
      when {
        remaining.inWholeDays > 0L -> {
          stringResource(R.string.less_than_days, listOf(remaining.inWholeDays + 1))
        }
        remaining.inWholeHours > 0L -> {
          stringResource(R.string.less_than_hours, listOf(remaining.inWholeHours + 1))
        }
        remaining.inWholeHours == 0L && remaining.inWholeMinutes >= 45L -> {
          stringResource(R.string.less_than_1_hour)
        }
        remaining.inWholeMinutes in 30..44 -> {
          stringResource(R.string.less_than_45_minutes)
        }
        remaining.inWholeMinutes in 15..29 -> {
          stringResource(R.string.less_than_30_minutes)
        }
        else -> {
          stringResource(R.string.soon)
        }
      },
    style = WidgetTextStyles.supportingText,
  )
}
