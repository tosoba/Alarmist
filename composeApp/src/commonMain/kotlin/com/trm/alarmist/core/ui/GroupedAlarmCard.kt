package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmListModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupedAlarmCard(
  alarm: AlarmListModel,
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  colors: CardColors = CardDefaults.cardColors(),
  isSelected: Boolean = false,
  onToggleAlarmSelection: () -> Unit = {},
) {
  Card(modifier = modifier, shape = shape, colors = colors, onClick = onToggleAlarmSelection) {
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = alarm.fireAtTime.toString(),
        style =
          MaterialTheme.typography.headlineMedium.run {
            if (isSelected) copy(fontWeight = FontWeight.Medium) else this
          },
      )

      alarm.name?.let {
        Text(
          modifier = Modifier.padding(horizontal = 16.dp),
          text = it,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Spacer(modifier = Modifier.weight(1f))

      Checkbox(checked = isSelected, onCheckedChange = { onToggleAlarmSelection() })
    }

    Spacer(modifier = Modifier.height(8.dp))
  }
}
