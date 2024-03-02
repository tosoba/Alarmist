package com.trm.alarmist.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
  onToggleExpandedClick: (Long) -> Unit = {},
) {
  ElevatedCard(modifier = modifier, shape = shape) {
    ExpandableHeaderRow(
      modifier =
        Modifier.fillMaxWidth()
          .clickable { onToggleExpandedClick(group.id) }
          .padding(vertical = 16.dp),
      isExpanded = isExpanded,
      transitionLabel = "${group.name}Header",
    ) {
      Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(group.name, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text("${group.alarmsCount} alarm(s)")
      }
    }
  }
}
