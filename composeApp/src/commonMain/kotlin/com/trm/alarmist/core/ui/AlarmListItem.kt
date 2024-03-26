package com.trm.alarmist.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.formatCountdown
import com.trm.alarmist.core.domain.model.AlarmListModel
import epicarchitect.calendar.compose.basis.daysOfWeekSortedBy
import epicarchitect.calendar.compose.basis.firstDayOfWeek
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun AlarmListItem(
  modifier: Modifier = Modifier,
  item: AlarmListModel,
  onItemClick: (AlarmListModel) -> Unit = {},
  onToggleOnOff: (AlarmListModel) -> Unit = {},
) {
  Card(
    modifier = modifier,
    colors =
      if (item.isOn) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
      } else {
        CardDefaults.cardColors()
      },
    onClick = { onItemClick(item) },
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    item.name?.let {
      Text(
        text = it,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodyLarge,
      )

      Spacer(modifier = Modifier.height(8.dp))
    }

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
      Text(
        text = item.fireAtTime.toString(),
        style =
          MaterialTheme.typography.displayMedium.run {
            if (item.isOn) copy(fontWeight = FontWeight.Medium) else this
          },
      )

      Spacer(modifier = Modifier.weight(1f))

      Switch(checked = item.isOn, onCheckedChange = { _ -> onToggleOnOff(item) })
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AlarmScheduleDescription(item)

      Spacer(modifier = Modifier.weight(1f))

      item.fireOnDateTime?.let {
        Countdown(
          targetEpochMillis = it.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        ) { remainingMillis ->
          AnimatedVisibility(remainingMillis >= 0L) {
            Text(
              text = remainingMillis.toDuration(DurationUnit.MILLISECONDS).formatCountdown(),
              style = MaterialTheme.typography.bodyLarge,
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun AlarmScheduleDescription(item: AlarmListModel, modifier: Modifier = Modifier) {
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
        )
      }
    }
  } else {
    Text("One time", overflow = TextOverflow.Ellipsis)
  }
}

@Composable
private fun Countdown(targetEpochMillis: Long, content: @Composable (remainingTime: Long) -> Unit) {
  var remainingTime by
    remember(targetEpochMillis) {
      mutableStateOf(targetEpochMillis - Clock.System.now().toEpochMilliseconds())
    }

  content(remainingTime)

  LaunchedEffect(remainingTime) {
    val diff = remainingTime - (targetEpochMillis - Clock.System.now().toEpochMilliseconds())
    delay(1_000L - diff)
    remainingTime = targetEpochMillis - Clock.System.now().toEpochMilliseconds()
  }
}
