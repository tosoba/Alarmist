package com.trm.alarmist.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.trm.alarmist.core.common.util.amPmString
import com.trm.alarmist.core.common.util.toFormattedString
import com.trm.alarmist.core.ui.theme.onOffContainer
import kotlinx.datetime.LocalTime

@Composable
fun AlarmFireAtTime(fireAtTime: LocalTime, isOn: Boolean, modifier: Modifier = Modifier) {
  Text(
    text =
      buildAnnotatedString {
        withStyle(
          SpanStyle(
            fontSize = MaterialTheme.typography.displayMedium.fontSize,
            fontStyle = MaterialTheme.typography.displayMedium.fontStyle,
            fontWeight =
              if (isOn) FontWeight.Medium else MaterialTheme.typography.displayMedium.fontWeight,
          )
        ) {
          append(fireAtTime.toFormattedString())
        }

        fireAtTime.amPmString().takeIf(String::isNotEmpty)?.let {
          withStyle(
            SpanStyle(
              fontSize = MaterialTheme.typography.bodyLarge.fontSize,
              fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
              fontWeight =
                if (isOn) FontWeight.Medium else MaterialTheme.typography.bodyLarge.fontWeight,
            )
          ) {
            append(it)
          }
        }
      },
    color = MaterialTheme.colorScheme.onOffContainer(isOn),
    modifier = modifier,
  )
}
