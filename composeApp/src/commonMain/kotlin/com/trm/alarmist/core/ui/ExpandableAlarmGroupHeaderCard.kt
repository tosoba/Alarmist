package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel

@Composable
fun ExpandableAlarmGroupHeaderCard(
  group: AlarmGroupModel,
  modifier: Modifier = Modifier,
  isExpanded: Boolean = false,
  shape: Shape =
    if (isExpanded) {
      ShapeDefaults.Medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
    } else {
      ShapeDefaults.Medium
    },
  trailing: @Composable () -> Unit = {
    Box(modifier = Modifier.padding(end = 16.dp)) {
      ExpandableIcon(
        isExpanded = isExpanded,
        transitionLabel = "${group.name}Header",
      )
    }
  },
) {
  Card(
    modifier = modifier,
    shape = shape,
    colors =
      if (group.isOn) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
      } else {
        CardDefaults.cardColors()
      },
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      val textColor =
        if (group.isOn) {
          MaterialTheme.colorScheme.onPrimaryContainer
        } else {
          MaterialTheme.colorScheme.onSecondaryContainer
        }

      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = group.name,
          style =
            MaterialTheme.typography.titleLarge.run {
              if (group.isOn) copy(fontWeight = FontWeight.Medium) else this
            },
          color = textColor,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = if (group.alarmsCount > 0L) "${group.alarmsCount} alarm(s)" else "Empty",
          style = MaterialTheme.typography.bodyLarge,
          color = textColor,
        )
      }

      Spacer(Modifier.weight(1f))

      trailing()
    }
  }
}
