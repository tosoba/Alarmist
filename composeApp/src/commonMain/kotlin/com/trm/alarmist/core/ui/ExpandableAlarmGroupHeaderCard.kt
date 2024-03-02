package com.trm.alarmist.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
    ExpandableIcon(isExpanded = isExpanded, transitionLabel = "${group.name}Header")
  },
  onToggleExpandedClick: (Long) -> Unit = {},
) {
  ElevatedCard(modifier = modifier, shape = shape) {
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .clickable { onToggleExpandedClick(group.id) }
          .padding(vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(Modifier.width(16.dp))

      Icon(Icons.Default.Folder, contentDescription = group.name)

      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(group.name, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text("${group.alarmsCount} alarm(s)")
      }

      Spacer(Modifier.weight(1f))

      trailing()
    }
  }
}
