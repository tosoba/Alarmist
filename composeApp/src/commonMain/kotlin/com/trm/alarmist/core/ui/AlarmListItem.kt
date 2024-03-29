package com.trm.alarmist.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.formatCountdown
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.theme.onOffCardColors
import com.trm.alarmist.core.ui.theme.onOffContainer
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
  item: AlarmListModel,
  modifier: Modifier = Modifier,
  group: AlarmGroupModel? = null,
  shape: Shape = CardDefaults.shape,
  onItemClick: (AlarmListModel) -> Unit = {},
  onToggleOnOff: (AlarmListModel) -> Unit = {},
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.onOffCardColors(item.isOn),
    shape = shape,
    onClick = { onItemClick(item) },
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    AlarmLabel(
      item = item,
      group = group,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    )

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
      AlarmFireAtTime(item)
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
      AlarmFireOnDateTimeCountdown(item)
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun AlarmFireOnDateTimeCountdown(item: AlarmListModel, modifier: Modifier = Modifier) {
  item.fireOnDateTime?.let {
    Countdown(
      targetEpochMillis = it.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    ) { remainingMillis ->
      AnimatedVisibility(remainingMillis >= 0L, modifier = modifier) {
        Text(
          text = remainingMillis.toDuration(DurationUnit.MILLISECONDS).formatCountdown(),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onOffContainer(item.isOn),
        )
      }
    }
  }
}

@Composable
private fun AlarmFireAtTime(item: AlarmListModel, modifier: Modifier = Modifier) {
  Text(
    text = item.fireAtTime.toString(),
    modifier = modifier,
    style =
      MaterialTheme.typography.displayMedium.run {
        if (item.isOn) copy(fontWeight = FontWeight.Medium) else this
      },
    color = MaterialTheme.colorScheme.onOffContainer(item.isOn),
  )
}

@Composable
private fun AlarmLabel(
  item: AlarmListModel,
  group: AlarmGroupModel?,
  modifier: Modifier = Modifier,
) {
  val textColor = MaterialTheme.colorScheme.onOffContainer(item.isOn)

  if (item.name != null || group != null) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
      if (group != null) {
        Box(
          modifier =
            Modifier.size(24.dp)
              .background(color = Color(group.color), shape = RoundedCornerShape(8.dp))
              .border(width = 0.5.dp, color = textColor, shape = RoundedCornerShape(8.dp))
              .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.width(16.dp))
      }

      Text(
        text =
          if (item.name != null && group != null) {
            buildString {
              append(group.name)
              append(" - ")
              append(item.name)
            }
          } else {
            group?.name ?: item.name.orEmpty()
          },
        style = MaterialTheme.typography.bodyLarge,
        color = textColor,
      )
    }

    Spacer(modifier = Modifier.height(8.dp))
  }
}

@Composable
private fun AlarmScheduleDescription(item: AlarmListModel, modifier: Modifier = Modifier) {
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
    Text("One time", overflow = TextOverflow.Ellipsis, color = textColor)
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
