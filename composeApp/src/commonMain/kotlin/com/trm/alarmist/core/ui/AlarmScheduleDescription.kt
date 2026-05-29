package com.trm.alarmist.core.ui

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.and_others
import alarmist.composeapp.generated.resources.one_time
import alarmist.composeapp.generated.resources.paused_on
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.trm.alarmist.core.ui.calendar.basis.daysOfWeekSortedBy
import com.trm.alarmist.core.ui.calendar.basis.firstDayOfWeek
import com.trm.alarmist.core.ui.theme.onOffContainer
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlarmScheduleDescription(
  isOn: Boolean,
  scheduledOnDaysOfWeek: Collection<DayOfWeek>,
  scheduledOnDate: LocalDate?,
  offOnScheduledDate: Boolean,
  scheduledOnMultipleDates: Boolean,
  modifier: Modifier = Modifier,
) {
  if (scheduledOnDaysOfWeek.isNotEmpty() || scheduledOnDate != null) {
    Column(modifier = modifier) {
      if (scheduledOnDaysOfWeek.isNotEmpty()) {
        Text(
          buildAnnotatedString {
            daysOfWeekSortedBy(firstDayOfWeek()).forEachIndexed { index, dayOfWeek ->
              withStyle(
                SpanStyle(
                  fontWeight =
                    if (dayOfWeek in scheduledOnDaysOfWeek) FontWeight.Bold else FontWeight.Light
                )
              ) {
                append(dayOfWeek.name.take(2))
              }

              if (index != DayOfWeek.entries.lastIndex) {
                append(" ")
              }
            }
          },
          overflow = TextOverflow.Ellipsis,
          color = onOffContainer(isOn),
        )
      }

      if (
        scheduledOnDate != null &&
          (offOnScheduledDate || scheduledOnDate.dayOfWeek !in scheduledOnDaysOfWeek)
      ) {
        Text(
          buildAnnotatedString {
            if (offOnScheduledDate) {
              append(stringResource(Res.string.paused_on, scheduledOnDate.toString()))
            } else if (scheduledOnDate.dayOfWeek !in scheduledOnDaysOfWeek) {
              withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                append(scheduledOnDate.toString())
              }

              if (scheduledOnMultipleDates) {
                append(" ")
                append(stringResource(Res.string.and_others))
              }
            }
          },
          overflow = TextOverflow.Ellipsis,
          color = onOffContainer(isOn),
        )
      }
    }
  } else {
    Text(
      stringResource(Res.string.one_time),
      overflow = TextOverflow.Ellipsis,
      color = onOffContainer(isOn),
    )
  }
}
