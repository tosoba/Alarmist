package com.trm.alarmist.feature.timer

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.add_minute
import alarmist.composeapp.generated.resources.cancel_timer
import alarmist.composeapp.generated.resources.pause_timer
import alarmist.composeapp.generated.resources.reset_timer
import alarmist.composeapp.generated.resources.resume_timer
import alarmist.composeapp.generated.resources.stop_elapsed_timer_alarm
import alarmist.composeapp.generated.resources.subtract_minute
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.common.util.formatHMS
import com.trm.alarmist.core.domain.model.TimerState
import com.trm.alarmist.core.ui.DurationText
import com.trm.alarmist.core.ui.DurationTextLayoutType
import org.jetbrains.compose.resources.stringResource
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

      val cancelTimerContentDescription = stringResource(Res.string.cancel_timer)
      SmallFloatingActionButton(
        onClick = onCancelClick,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        modifier =
          Modifier.clearAndSetSemantics {
            contentDescription = cancelTimerContentDescription
            role = Role.Button
          },
      ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = null)
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    val elapsedDurationTextAlpha by
      rememberInfiniteTransition()
        .animateFloat(
          initialValue = 1f,
          targetValue = 0f,
          animationSpec =
            infiniteRepeatable(
              animation = tween(durationMillis = 750, easing = LinearEasing),
              repeatMode = RepeatMode.Reverse,
            ),
        )

    if (calculateWindowSizeClass().heightSizeClass == WindowHeightSizeClass.Compact) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(visible = state != TimerState.ELAPSED) {
          SmallFloatingActionButtonSizedSpacer()
        }

        DurationText(
          duration = duration,
          layoutType = DurationTextLayoutType.Horizontal,
          modifier =
            Modifier.padding(horizontal = 24.dp)
              .alpha(if (state != TimerState.ELAPSED) 1f else elapsedDurationTextAlpha),
        )

        AnimatedVisibility(visible = state != TimerState.ELAPSED) { TimerResetButton(onResetClick) }
      }
    } else {
      AnimatedVisibility(visible = state != TimerState.ELAPSED) {
        SmallFloatingActionButtonSizedSpacer()
      }

      DurationText(
        duration = duration,
        layoutType = DurationTextLayoutType.Vertical,
        modifier =
          Modifier.padding(vertical = 16.dp)
            .alpha(if (state != TimerState.ELAPSED) 1f else elapsedDurationTextAlpha),
      )

      AnimatedVisibility(state != TimerState.ELAPSED) { TimerResetButton(onResetClick) }
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
  val resetTimerContentDescription = stringResource(Res.string.reset_timer)
  SmallFloatingActionButton(
    onClick = onClick,
    elevation = FloatingActionButtonDefaults.loweredElevation(),
    modifier =
      Modifier.clearAndSetSemantics {
        contentDescription = resetTimerContentDescription
        role = Role.Button
      },
  ) {
    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
  }
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
      subtractMinuteVisible ->
      if (subtractMinuteVisible) {
        val subtractMinuteContentDescription = stringResource(Res.string.subtract_minute)
        FloatingActionButton(
          onClick = onSubtractMinuteClick,
          elevation = FloatingActionButtonDefaults.loweredElevation(),
          modifier =
            Modifier.clearAndSetSemantics {
              contentDescription = subtractMinuteContentDescription
              role = Role.Button
            },
        ) {
          Text("-1:00")
        }
      } else {
        FloatingActionButtonSizedSpacer()
      }
    }

    val playPauseContentDescription =
      stringResource(
        when (state) {
          TimerState.RUNNING -> Res.string.pause_timer
          TimerState.PAUSED -> Res.string.resume_timer
          else -> Res.string.stop_elapsed_timer_alarm
        }
      )
    LargeFloatingActionButton(
      onClick = if (state == TimerState.ELAPSED) onStopElapsedAlarmClick else onToggleRunningClick,
      modifier =
        Modifier.clearAndSetSemantics {
          contentDescription = playPauseContentDescription
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

    AnimatedContent(targetState = state != TimerState.ELAPSED) { addMinuteVisible ->
      if (addMinuteVisible) {
        val addMinuteContentDescription = stringResource(Res.string.add_minute)
        FloatingActionButton(
          onClick = onAddMinuteClick,
          elevation = FloatingActionButtonDefaults.loweredElevation(),
          modifier =
            Modifier.clearAndSetSemantics {
              contentDescription = addMinuteContentDescription
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
