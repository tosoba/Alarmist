package com.trm.alarmist.feature.stopwatch

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
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
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // TODO: different layouts for different screen sizes

    Spacer(modifier = Modifier.weight(1f))

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

    Spacer(modifier = Modifier.weight(1f))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
    ) {
      AnimatedContent(
        targetState = state != StopwatchState.IDLE,
        transitionSpec =
          AnimatedContentTransitionScope<Boolean>::sideFloatingActionButtonTransitionSpec,
      ) {
        if (it) {
          FloatingActionButton(
            onClick = onCancelClick,
            elevation = FloatingActionButtonDefaults.loweredElevation(),
          ) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset stopwatch")
          }
        } else {
          FloatingActionButtonSpacerBox()
        }
      }

      LargeFloatingActionButton(onClick = onStartStopClick) {
        Icon(
          imageVector =
            if (state == StopwatchState.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription =
            when (state) {
              StopwatchState.RUNNING -> "Pause stopwatch"
              StopwatchState.PAUSED -> "Resume stopwatch"
              else -> "Start stopwatch"
            },
        )
      }

      // TODO: on lap click
      AnimatedContent(
        targetState = state == StopwatchState.RUNNING,
        transitionSpec =
          AnimatedContentTransitionScope<Boolean>::sideFloatingActionButtonTransitionSpec,
      ) {
        if (it) {
          FloatingActionButton(
            onClick = {},
            elevation = FloatingActionButtonDefaults.loweredElevation(),
          ) {
            Icon(imageVector = Icons.Default.Timer, contentDescription = "Record lap")
          }
        } else {
          FloatingActionButtonSpacerBox()
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }

  // TODO: scrollable lap list
}

@Composable
private fun FloatingActionButtonSpacerBox() {
  Box(
    modifier =
      Modifier.size(56.dp).clip(FloatingActionButtonDefaults.shape).background(Color.Transparent)
  )
}

private fun <S> AnimatedContentTransitionScope<S>.sideFloatingActionButtonTransitionSpec():
  ContentTransform =
  scaleIn(animationSpec = tween(220)).togetherWith(scaleOut(animationSpec = tween(90)))
