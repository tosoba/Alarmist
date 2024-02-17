package com.trm.alarmist.feature.alarms.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.formatCountdown
import com.trm.alarmist.core.domain.model.AlarmListItem
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun AlarmListContent(modifier: Modifier = Modifier, component: AlarmListComponent) {
  val alarms by component.alarms.collectAsState(emptyList())
  LazyColumn(
    modifier = modifier,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
  ) {
    items(alarms) {
      AlarmListItem(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        item = it,
        onItemClick = {},
        onToggleOnOff = component::onToggleAlarmOnOff,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmListItem(
  modifier: Modifier = Modifier,
  item: AlarmListItem,
  onItemClick: (AlarmListItem) -> Unit = {},
  onToggleOnOff: (AlarmListItem) -> Unit = {},
) {
  Card(modifier = modifier, onClick = { onItemClick(item) }) {
    Spacer(modifier = Modifier.height(8.dp))

    item.name?.let { Text(modifier = Modifier.padding(horizontal = 8.dp), text = it) }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = item.fireAt.toString(), style = MaterialTheme.typography.headlineLarge)
      Spacer(modifier = Modifier.weight(1f))
      Icon(imageVector = Icons.Default.Edit, contentDescription = null)
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      item.nextFireOnDateTime?.let {
        Countdown(
          targetEpochMillis = it.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        ) { remainingMillis ->
          Text(
            "Will fire in: ${remainingMillis.toDuration(DurationUnit.MILLISECONDS).formatCountdown()}"
          )
        }
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
