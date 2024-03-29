package com.trm.alarmist.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.ui.theme.onOffContainer

@Composable
fun AlarmFireAtTime(item: AlarmListModel, modifier: Modifier = Modifier) {
  Text(
    text = item.fireAtTime.toString(),
    modifier = modifier,
    style =
      MaterialTheme.typography.displayMedium.run {
        if (item.isOn) copy(fontWeight = FontWeight.Medium) else this
      },
    color = MaterialTheme.colorScheme.onOffContainer(item.isOn),
  )
}
