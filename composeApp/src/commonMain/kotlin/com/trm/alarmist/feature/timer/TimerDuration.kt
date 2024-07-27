package com.trm.alarmist.feature.timer

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.formatHMS
import com.trm.alarmist.core.common.util.zeroPadded
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TimerDuration(
  duration: Duration,
  initialDuration: Duration,
  isRunning: Boolean,
  onToggleRunningClick: () -> Unit,
  onCancelClick: () -> Unit,
  onResetClick: () -> Unit,
  onAddMinuteClick: () -> Unit,
  onSubtractMinuteClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "${initialDuration.formatHMS()} Timer",
        style =
          TextStyle(
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
          ),
      )

      Spacer(modifier = Modifier.weight(1f))

      SmallFloatingActionButton(onClick = onCancelClick) {
        Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel timer")
      }
    }

    val windowSizeClass = calculateWindowSizeClass()
    if (
      windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact ||
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    ) {
      Spacer(modifier = Modifier.weight(1f))

      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(32.dp))
        // TODO: move this to center of the screen for WindowWidthSizeClass.Expanded
        Column(horizontalAlignment = Alignment.End) {
          TimerDuration(duration = duration)
          Spacer(modifier = Modifier.height(16.dp))
          TimerResetButton(onResetClick)
        }

        Spacer(modifier = Modifier.weight(1f))

        TimerDurationControls(
          duration = duration,
          isRunning = isRunning,
          onToggleRunningClick = onToggleRunningClick,
          onAddMinuteClick = onAddMinuteClick,
          onSubtractMinuteClick = onSubtractMinuteClick,
        )

        Spacer(modifier = Modifier.width(32.dp))
      }
    } else {
      Spacer(modifier = Modifier.weight(1f))

      TimerDuration(duration = duration)
      Spacer(modifier = Modifier.height(16.dp))
      TimerResetButton(onResetClick)

      Spacer(modifier = Modifier.weight(1f))

      TimerDurationControls(
        duration = duration,
        isRunning = isRunning,
        onToggleRunningClick = onToggleRunningClick,
        onAddMinuteClick = onAddMinuteClick,
        onSubtractMinuteClick = onSubtractMinuteClick,
        modifier = Modifier.fillMaxWidth(),
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun TimerResetButton(onResetClick: () -> Unit) {
  FloatingActionButton(
    onClick = onResetClick,
    elevation = FloatingActionButtonDefaults.loweredElevation(),
  ) {
    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset timer")
  }
}

@Composable
private fun TimerDuration(duration: Duration) {
  val (time, fractionOfSecond) =
    remember(duration) {
      duration.toComponents { hours, minutes, seconds, nanoseconds ->
        buildString {
          if (hours > 0L) {
            append(hours.toInt())
            append(':')
          }
          if (hours > 0L || minutes > 0) {
            append(minutes)
            append(':')
          }
          append(seconds.zeroPadded())
        } to (nanoseconds / 10_000_000L).toInt().zeroPadded()
      }
    }

  Column(horizontalAlignment = Alignment.End) {
    Text(
      text = time,
      style =
        TextStyle(
          fontSize = MaterialTheme.typography.headlineLarge.fontSize,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        ),
    )
    Text(
      text = fractionOfSecond,
      style =
        TextStyle(
          fontSize = MaterialTheme.typography.headlineMedium.fontSize,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
        ),
    )
  }
}

@Composable
private fun TimerDurationControls(
  duration: Duration,
  isRunning: Boolean,
  onToggleRunningClick: () -> Unit,
  onAddMinuteClick: () -> Unit,
  onSubtractMinuteClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
  ) {
    AnimatedContent(targetState = duration > 1.minutes) {
      if (it) {
        FloatingActionButton(
          onClick = onSubtractMinuteClick,
          elevation = FloatingActionButtonDefaults.loweredElevation(),
          modifier =
            Modifier.semantics { contentDescription = "Subtract 1 minute from timer duration" },
        ) {
          Text("-1:00")
        }
      } else {
        FloatingActionButtonSpacerBox()
      }
    }

    LargeFloatingActionButton(onClick = onToggleRunningClick) {
      Icon(
        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
        contentDescription = if (isRunning) "Pause timer" else "Resume timer",
      )
    }

    FloatingActionButton(
      onClick = onAddMinuteClick,
      elevation = FloatingActionButtonDefaults.loweredElevation(),
      modifier = Modifier.semantics { contentDescription = "Add 1 minute to timer duration" },
    ) {
      Text("+1:00")
    }
  }
}

@Composable
private fun FloatingActionButtonSpacerBox() {
  Spacer(modifier = Modifier.size(56.dp))
}
