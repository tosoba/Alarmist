package com.trm.alarmist.feature.stopwatch

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    modifier =
      Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(30.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // TODO: different layouts for different screens

    Spacer(modifier = Modifier.weight(1f))

    val (hours, minutes, seconds, fraction) =
      remember(duration) { duration.toComponents(::DurationComponents) }

    // TODO: varying sizes for duration components
    // TODO: hide component if for example hour == 0
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

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
    ) {
      // TODO: consider replacing AnimatedContent with shrink size to 0?
      AnimatedContent(state != StopwatchState.IDLE) {
        if (it) {
          FloatingActionButton(onClick = onCancelClick) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset stopwatch")
          }
        } else {
          Box(
            modifier =
              Modifier.size(56.dp)
                .clip(FloatingActionButtonDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
          )
        }
      }

      LargeFloatingActionButton(onClick = onStartStopClick) {
        Icon(
          imageVector =
            if (state == StopwatchState.STARTED) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription =
            when (state) {
              StopwatchState.STARTED -> "Pause stopwatch"
              StopwatchState.STOPPED -> "Resume stopwatch"
              else -> "Start stopwatch"
            },
        )
      }

      // TODO: on lap click
      AnimatedContent(state == StopwatchState.STARTED) {
        if (it) {
          FloatingActionButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Timer, contentDescription = "Record lap")
          }
        } else {
          Box(
            modifier =
              Modifier.size(56.dp)
                .clip(FloatingActionButtonDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
          )
        }
      }
    }
  }

  // TODO: scrollable lap list
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
