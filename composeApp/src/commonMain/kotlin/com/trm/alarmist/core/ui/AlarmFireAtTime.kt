package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.toFormattedString
import com.trm.alarmist.core.ui.theme.onOffContainer
import kotlinx.datetime.LocalTime

@Composable
fun AlarmFireAtTime(fireAtTime: LocalTime, isOn: Boolean, modifier: Modifier = Modifier) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    AutoSizeText(
      text = fireAtTime.toFormattedString(),
      style =
        MaterialTheme.typography.displayMedium.copy(
          fontWeight =
            if (isOn) FontWeight.Medium else MaterialTheme.typography.displayMedium.fontWeight
        ),
      color = onOffContainer(isOn),
      maxLines = 1,
      maxTextSize = MaterialTheme.typography.displayMedium.fontSize,
      modifier = Modifier.alignByBaseline(),
    )

    fireAtTime.amPmString().takeIf(String::isNotEmpty)?.let {
      Spacer(modifier = Modifier.width(2.dp))

      Text(
        text = it,
        style =
          MaterialTheme.typography.bodyLarge.copy(
            fontWeight =
              if (isOn) FontWeight.Medium else MaterialTheme.typography.bodyLarge.fontWeight
          ),
        color = onOffContainer(isOn),
        maxLines = 1,
        modifier = Modifier.alignByBaseline(),
      )
    }
  }
}
