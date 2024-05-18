package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.ui.theme.onOffContainer

@Composable
fun AlarmLabel(
  alarmName: String?,
  isOn: Boolean,
  group: AlarmGroupModel?,
  modifier: Modifier = Modifier,
) {
  if (alarmName != null || group != null) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
      if (group != null) {
        AlarmGroupIcon(color = group.color, size = 32.dp)
        Spacer(Modifier.width(12.dp))
      }

      Text(
        text =
          if (alarmName != null && group != null) {
            buildString {
              append(group.name)
              append(" · ")
              append(alarmName)
            }
          } else {
            group?.name ?: alarmName.orEmpty()
          },
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onOffContainer(isOn),
      )
    }
  }
}
