package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListStatus
import com.trm.alarmist.core.ui.theme.onOffCardColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingAlarmListItem(
  item: UpcomingAlarmListModel,
  modifier: Modifier = Modifier,
  group: AlarmGroupModel? = null,
  shape: Shape = CardDefaults.shape,
  onItemClick: (UpcomingAlarmListModel) -> Unit = {},
  onOffButtonClick: (UpcomingAlarmListModel) -> Unit = {},
  onOffOnDateButtonClick: (UpcomingAlarmListModel) -> Unit = {},
  onOnButtonClick: (UpcomingAlarmListModel) -> Unit = {},
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.onOffCardColors(item.status == UpcomingAlarmListStatus.ON),
    shape = shape,
    onClick = { onItemClick(item) },
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    AlarmLabel(
      alarmName = item.name,
      isOn = item.status == UpcomingAlarmListStatus.ON,
      group = group,
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
    )

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
      AlarmFireAtTime(
        fireAtTime = item.fireAtTime,
        isOn = item.status == UpcomingAlarmListStatus.ON,
      )

      Spacer(modifier = Modifier.weight(1f))

      if (item.scheduledOnDaysOfWeek.isEmpty() && item.scheduledOnDate == null) {
        Switch(
          checked = item.status == UpcomingAlarmListStatus.ON,
          onCheckedChange = { _ ->
            if (item.status == UpcomingAlarmListStatus.ON) onOnButtonClick(item)
            else onOffButtonClick(item)
          },
        )
      } else {
        SingleChoiceSegmentedButtonRow {
          SegmentedButton(
            selected = item.status == UpcomingAlarmListStatus.OFF,
            onClick = {
              if (item.status != UpcomingAlarmListStatus.OFF) {
                onOffButtonClick(item)
              }
            },
            shape =
              RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 0.dp,
                bottomEnd = 0.dp,
                bottomStart = 20.dp,
              ),
            label = { Icon(Icons.Default.Stop, "Off") },
          )

          SegmentedButton(
            selected = item.status == UpcomingAlarmListStatus.OFF_ON_DATE,
            onClick = {
              if (item.status != UpcomingAlarmListStatus.OFF_ON_DATE) {
                onOffOnDateButtonClick(item)
              }
            },
            shape = RectangleShape,
            label = { Icon(Icons.Default.Pause, "Pause") },
          )

          SegmentedButton(
            selected = item.status == UpcomingAlarmListStatus.ON,
            onClick = {
              if (item.status != UpcomingAlarmListStatus.ON) {
                onOnButtonClick(item)
              }
            },
            shape =
              RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 20.dp,
                bottomEnd = 20.dp,
                bottomStart = 0.dp,
              ),
            label = { Icon(Icons.Default.PlayArrow, "On") },
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AlarmScheduleDescription(
        isOn = item.status == UpcomingAlarmListStatus.ON,
        scheduledOnDaysOfWeek = item.scheduledOnDaysOfWeek,
        scheduledOnDate = item.scheduledOnDate,
        scheduledOnMultipleDates = item.scheduledOnMultipleDates,
      )

      Spacer(modifier = Modifier.weight(1f))

      AlarmFireOnDateTimeCountdown(item.fireOnDateTime, item.status == UpcomingAlarmListStatus.ON)
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
