package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.elevatedIf
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.theme.onOffCardColors

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
    elevation = CardDefaults.elevatedIf(item.isOn),
    shape = shape,
    onClick = { onItemClick(item) },
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    AlarmLabel(
      alarmName = item.name,
      isOn = item.isOn,
      group = group,
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
    )

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      AlarmFireAtTime(fireAtTime = item.nextFireAtTime, isOn = item.isOn)
      Spacer(modifier = Modifier.width(8.dp))
      Switch(checked = item.isOn, onCheckedChange = { _ -> onToggleOnOff(item) })
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AlarmScheduleDescription(
        isOn = item.isOn,
        scheduledOnDaysOfWeek = item.scheduledOnDaysOfWeek,
        scheduledOnDate = item.closestScheduledOnDate,
        offOnScheduledDate = item.offOnAllScheduledDates,
        scheduledOnMultipleDates = item.scheduledOnMultipleDates,
      )

      Spacer(modifier = Modifier.weight(1f))

      AlarmFireOnDateTimeCountdown(fireOnDateTime = item.fireOnDateTime)
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
