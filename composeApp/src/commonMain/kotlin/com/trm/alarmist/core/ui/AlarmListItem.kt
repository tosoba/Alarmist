package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.formatCountdown
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
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

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
      Column {
        Text(
          text = item.fireAtTime.toString(),
          style =
            MaterialTheme.typography.headlineLarge.run {
              if (item.isOn) copy(fontWeight = FontWeight.Medium) else this
            },
        )

        Spacer(modifier = Modifier.height(4.dp))

        item.name?.let { Text(it) }
      }

      Spacer(modifier = Modifier.weight(1f))

      Switch(
        checked = item.isOn,
        onCheckedChange = { _ -> onToggleOnOff(item) },
        thumbContent = {
          Icon(
            imageVector = if (item.isOn) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
          )
        },
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(item.scheduleDescription, maxLines = 2, overflow = TextOverflow.Ellipsis)

      Spacer(modifier = Modifier.weight(1f))

      item.nextFireOnDateTime?.let {
        Countdown(
          targetEpochMillis = it.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        ) { remainingMillis ->
          Text(text = remainingMillis.toDuration(DurationUnit.MILLISECONDS).formatCountdown())
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
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
