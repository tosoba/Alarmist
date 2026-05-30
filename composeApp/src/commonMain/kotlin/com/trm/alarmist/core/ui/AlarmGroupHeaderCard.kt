package com.trm.alarmist.core.ui

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarms_count
import alarmist.composeapp.generated.resources.empty
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.trm.alarmist.core.ui.theme.onOffCardBorder
import com.trm.alarmist.core.ui.theme.onOffCardColors
import com.trm.alarmist.core.ui.theme.onOffContainer
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlarmGroupHeaderCard(
  group: AlarmGroupModel,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = ShapeDefaults.Medium,
  trailing: @Composable () -> Unit,
) {
  Card(
    modifier = modifier,
    onClick = onClick,
    shape = shape,
    colors = CardDefaults.onOffCardColors(group.isOn),
    border = CardDefaults.onOffCardBorder(group.isOn),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(Modifier.width(16.dp))

      AlarmGroupIcon(color = group.color, size = 45.dp)

      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = group.name,
          style =
            MaterialTheme.typography.titleLarge.run {
              if (group.isOn) copy(fontWeight = FontWeight.Medium) else this
            },
          color = onOffContainer(group.isOn),
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text =
            if (group.alarmsCount > 0L) stringResource(Res.string.alarms_count, group.alarmsCount)
            else stringResource(Res.string.empty),
          style = MaterialTheme.typography.bodyLarge,
          color = onOffContainer(group.isOn),
        )
      }

      Spacer(Modifier.weight(1f))

      trailing()
    }
  }
}
