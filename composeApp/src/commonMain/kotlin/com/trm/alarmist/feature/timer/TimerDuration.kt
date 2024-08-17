package com.trm.alarmist.feature.timer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trm.alarmist.core.common.util.formatHMS
import com.trm.alarmist.core.common.util.zeroPadded
import com.trm.alarmist.core.domain.model.TimerState
import com.trm.alarmist.core.ui.AutoSizeText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TimerDuration(
  duration: Duration,
  initialDuration: Duration,
  state: TimerState,
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
        text = initialDuration.formatHMS(),
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium),
      )

      Spacer(modifier = Modifier.weight(1f))

      SmallFloatingActionButton(
        onClick = onCancelClick,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        modifier =
          Modifier.clearAndSetSemantics {
            contentDescription = "Cancel timer"
            role = Role.Button
          },
      ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = null)
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    if (calculateWindowSizeClass().heightSizeClass == WindowHeightSizeClass.Compact) {
      AnimatedContent(targetState = state != TimerState.ELAPSED) {
        if (it) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            SmallFloatingActionButtonSizedSpacer()
            Spacer(modifier = Modifier.width(24.dp))
            TimerDuration(duration = duration, layoutType = TimerDurationLayoutType.Horizontal)
            Spacer(modifier = Modifier.width(24.dp))
            TimerResetButton(onResetClick)
          }
        } else {
          TimerDuration(duration = duration, layoutType = TimerDurationLayoutType.Horizontal)
        }
      }
    } else {
      TimerDuration(duration = duration, layoutType = TimerDurationLayoutType.Vertical)
      AnimatedVisibility(state != TimerState.ELAPSED) {
        Column {
          Spacer(modifier = Modifier.height(16.dp))
          TimerResetButton(onResetClick)
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    TimerDurationControls(
      duration = duration,
      state = state,
      onToggleRunningClick = onToggleRunningClick,
      onAddMinuteClick = onAddMinuteClick,
      onSubtractMinuteClick = onSubtractMinuteClick,
      onStopElapsedAlarmClick = onResetClick,
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))
  }
}

@Composable
private fun TimerResetButton(onClick: () -> Unit) {
  SmallFloatingActionButton(
    onClick = onClick,
    elevation = FloatingActionButtonDefaults.loweredElevation(),
    modifier =
      Modifier.clearAndSetSemantics {
        contentDescription = "Reset timer"
        role = Role.Button
      },
  ) {
    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
  }
}

private enum class TimerDurationLayoutType {
  Vertical,
  Horizontal,
}

@Composable
private fun TimerDuration(duration: Duration, layoutType: TimerDurationLayoutType) {
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

  when (layoutType) {
    TimerDurationLayoutType.Vertical -> {
      Column(horizontalAlignment = Alignment.End) {
        TimeText(text = time)
        FractionOfSecondText(text = fractionOfSecond)
      }
    }
    TimerDurationLayoutType.Horizontal -> {
      Row(verticalAlignment = Alignment.Bottom) {
        TimeText(text = time, modifier = Modifier.alignByBaseline())
        Spacer(modifier = Modifier.width(8.dp))
        FractionOfSecondText(text = fractionOfSecond, modifier = Modifier.alignByBaseline())
      }
    }
  }
}

@Composable
private fun TimeText(text: String, modifier: Modifier = Modifier) {
  AutoSizeText(
    text = text,
    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
    modifier = modifier,
    maxTextSize = 72.sp,
  )
}

@Composable
private fun FractionOfSecondText(text: String, modifier: Modifier = Modifier) {
  AutoSizeText(
    text = text,
    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
    modifier = modifier,
    maxTextSize = 36.sp,
  )
}

@Composable
private fun TimerDurationControls(
  duration: Duration,
  state: TimerState,
  onToggleRunningClick: () -> Unit,
  onAddMinuteClick: () -> Unit,
  onSubtractMinuteClick: () -> Unit,
  onStopElapsedAlarmClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
  ) {
    AnimatedContent(targetState = state != TimerState.ELAPSED && duration > 1.minutes) {
      if (it) {
        FloatingActionButton(
          onClick = onSubtractMinuteClick,
          elevation = FloatingActionButtonDefaults.loweredElevation(),
          modifier =
            Modifier.clearAndSetSemantics {
              contentDescription = "Subtract 1 minute from timer duration"
              role = Role.Button
            },
        ) {
          Text("-1:00")
        }
      } else {
        FloatingActionButtonSizedSpacer()
      }
    }

    LargeFloatingActionButton(
      onClick = if (state == TimerState.ELAPSED) onStopElapsedAlarmClick else onToggleRunningClick,
      modifier =
        Modifier.clearAndSetSemantics {
          contentDescription =
            when (state) {
              TimerState.RUNNING -> "Pause timer"
              TimerState.PAUSED -> "Resume timer"
              else -> "Stop elapsed timer alarm"
            }
          role = Role.Button
        },
    ) {
      Icon(
        imageVector =
          when (state) {
            TimerState.RUNNING -> Icons.Default.Pause
            TimerState.PAUSED -> Icons.Default.PlayArrow
            else -> Icons.Default.Stop
          },
        contentDescription = null,
      )
    }

    AnimatedContent(targetState = state != TimerState.ELAPSED) {
      if (it) {
        FloatingActionButton(
          onClick = onAddMinuteClick,
          elevation = FloatingActionButtonDefaults.loweredElevation(),
          modifier =
            Modifier.clearAndSetSemantics {
              contentDescription = "Add 1 minute to timer duration"
              role = Role.Button
            },
        ) {
          Text("+1:00")
        }
      } else {
        FloatingActionButtonSizedSpacer()
      }
    }
  }
}

@Composable
private fun FloatingActionButtonSizedSpacer() {
  Spacer(modifier = Modifier.size(56.dp))
}

@Composable
private fun SmallFloatingActionButtonSizedSpacer() {
  Spacer(modifier = Modifier.size(40.dp))
}
