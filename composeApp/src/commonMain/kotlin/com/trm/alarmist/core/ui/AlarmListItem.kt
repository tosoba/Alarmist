package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.theme.onOffCardColors
import com.trm.alarmist.core.ui.theme.onOffContainer

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
private fun AlarmLabel(
  item: AlarmListModel,
  group: AlarmGroupModel?,
  modifier: Modifier = Modifier,
) {
  if (item.name != null || group != null) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
      if (group != null) {
        AlarmGroupIcon(group.color)
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
        color = MaterialTheme.colorScheme.onOffContainer(item.isOn),
      )
    }
  }
}
