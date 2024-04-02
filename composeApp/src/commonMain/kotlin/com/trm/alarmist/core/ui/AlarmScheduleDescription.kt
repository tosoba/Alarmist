package com.trm.alarmist.core.ui

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.one_time
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.theme.onOffContainer
import epicarchitect.calendar.compose.basis.daysOfWeekSortedBy
import epicarchitect.calendar.compose.basis.firstDayOfWeek
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AlarmScheduleDescription(item: AlarmListModel, modifier: Modifier = Modifier) {
  val textColor = MaterialTheme.colorScheme.onOffContainer(item.isOn)

  if (item.scheduledOnDaysOfWeek.isNotEmpty() || item.scheduledOnClosestDate != null) {
    Column(modifier = modifier) {
      if (item.scheduledOnDaysOfWeek.isNotEmpty()) {
        Text(
          buildAnnotatedString {
            daysOfWeekSortedBy(firstDayOfWeek()).forEachIndexed { index, dayOfWeek ->
              if (dayOfWeek in item.scheduledOnDaysOfWeek) {
                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                  append(dayOfWeek.name.take(2))
                }
              } else {
                withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                  append(dayOfWeek.name.take(2))
                }
              }
              if (index != DayOfWeek.entries.lastIndex) {
                append(" ")
              }
            }
          },
          overflow = TextOverflow.Ellipsis,
          color = textColor,
        )
      }

      if (item.scheduledOnClosestDate != null) {
        Text(
          buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
              append(item.scheduledOnClosestDate.toString())
            }
            if (item.scheduledOnMultipleDates) {
              append(" and others")
            }
          },
          overflow = TextOverflow.Ellipsis,
          color = textColor,
        )
      }
    }
  } else {
    Text(stringResource(Res.string.one_time), overflow = TextOverflow.Ellipsis, color = textColor)
  }
}
