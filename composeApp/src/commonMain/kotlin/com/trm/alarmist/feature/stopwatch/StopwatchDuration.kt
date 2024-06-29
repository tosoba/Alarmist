package com.trm.alarmist.feature.stopwatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.zeroPadded
import com.trm.alarmist.core.domain.model.StopwatchState
import kotlin.time.Duration

@Composable
fun StopwatchDuration(
  duration: Duration,
  state: StopwatchState,
  onStartStopClick: () -> Unit,
  onCancelClick: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(30.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.weight(1f))

    val (hours, minutes, seconds, fraction) =
      remember(duration) { duration.toComponents(::DurationComponents) }

    Text(
      text = hours,
      style =
        TextStyle(
          fontSize = MaterialTheme.typography.headlineLarge.fontSize,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        ),
    )

    Text(
      text = minutes,
      style =
        TextStyle(
          fontSize = MaterialTheme.typography.headlineLarge.fontSize,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        ),
    )

    Text(
      text = seconds,
      style =
        TextStyle(
          fontSize = MaterialTheme.typography.headlineLarge.fontSize,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        ),
    )

    Text(
      text = fraction,
      style =
        TextStyle(
          fontSize = MaterialTheme.typography.headlineLarge.fontSize,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        ),
    )

    Spacer(modifier = Modifier.weight(1f))

    Row(modifier = Modifier.fillMaxWidth()) {
      Button(modifier = Modifier.weight(1f), onClick = onStartStopClick) {
        Text(
          text =
            when (state) {
              StopwatchState.STARTED -> "Stop"
              StopwatchState.STOPPED -> "Resume"
              else -> "Start"
            }
        )
      }

      Spacer(modifier = Modifier.width(30.dp))

      Button(
        modifier = Modifier.weight(1f),
        onClick = onCancelClick,
        enabled = state != StopwatchState.IDLE,
      ) {
        Text(text = "Cancel")
      }
    }
  }
}

private data class DurationComponents(
  val hours: String,
  val minutes: String,
  val seconds: String,
  val fraction: String,
) {
  constructor(
    hours: Long,
    minutes: Int,
    seconds: Int,
    nanoseconds: Int,
  ) : this(
    hours = hours.toInt().zeroPadded(),
    minutes = minutes.zeroPadded(),
    seconds = seconds.zeroPadded(),
    fraction = (nanoseconds / 10_000_000L).toInt().zeroPadded(),
  )
}
