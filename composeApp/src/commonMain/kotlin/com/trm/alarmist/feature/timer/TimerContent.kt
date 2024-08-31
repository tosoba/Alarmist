package com.trm.alarmist.feature.timer

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.alarmist.core.domain.model.TimerState
import kotlin.time.Duration

@Composable expect fun TimerContent(component: TimerComponent, modifier: Modifier = Modifier)

@Composable
fun TimerScaffold(
  modifier: Modifier = Modifier,
  duration: Duration,
  initialDuration: Duration,
  state: TimerState,
  onStartClick: (Duration) -> Unit,
  onToggleRunningClick: () -> Unit,
  onCancelClick: () -> Unit,
  onResetClick: () -> Unit,
  onAddMinuteClick: () -> Unit,
  onSubtractMinuteClick: () -> Unit,
) {
  Scaffold(modifier = modifier) { padding ->
    Crossfade(targetState = state == TimerState.IDLE, label = "TimerContent") { isIdle ->
      if (isIdle) {
        TimerInput(
          modifier =
            Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .padding(bottom = padding.calculateBottomPadding()),
          onStartClick = onStartClick,
        )
      } else {
        TimerDuration(
          modifier =
            Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .padding(bottom = padding.calculateBottomPadding()),
          duration = duration,
          initialDuration = initialDuration,
          state = state,
          onToggleRunningClick = onToggleRunningClick,
          onCancelClick = onCancelClick,
          onResetClick = onResetClick,
          onAddMinuteClick = onAddMinuteClick,
          onSubtractMinuteClick = onSubtractMinuteClick,
        )
      }
    }
  }
}
