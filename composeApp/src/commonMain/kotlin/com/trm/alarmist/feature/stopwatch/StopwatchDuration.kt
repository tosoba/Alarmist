package com.trm.alarmist.feature.stopwatch

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.domain.model.StopwatchState
import com.trm.alarmist.core.ui.DurationText
import com.trm.alarmist.core.ui.DurationTextLayoutType
import com.trm.alarmist.core.ui.rememberDurationText
import com.trm.alarmist.core.ui.sideFloatingActionButtonTransitionSpec
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun StopwatchDuration(
  duration: Duration,
  state: StopwatchState,
  laps: SnapshotStateList<Duration>,
  onStartStopClick: () -> Unit,
  onCancelClick: () -> Unit,
  onRecordLapClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(modifier = modifier) {
    if (calculateWindowSizeClass().heightSizeClass == WindowHeightSizeClass.Compact) {
      Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        Column(
          modifier = Modifier.fillMaxHeight().weight(1f),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Spacer(modifier = Modifier.weight(1f))

          DurationText(
            duration = duration,
            layoutType = DurationTextLayoutType.Horizontal,
            modifier = Modifier.padding(horizontal = 24.dp),
          )

          Spacer(modifier = Modifier.weight(1f))

          StopwatchDurationControls(
            state = state,
            onStartStopClick = onStartStopClick,
            onCancelClick = onCancelClick,
            onRecordLapClick = onRecordLapClick,
          )

          Spacer(modifier = Modifier.height(16.dp))
        }

        if (laps.isNotEmpty()) {
          Spacer(modifier = Modifier.width(32.dp))

          val lapsState = rememberLazyListState()
          LaunchedEffect(laps.size) { lapsState.animateScrollToItem(laps.lastIndex) }

          Laps(
            laps = laps,
            state = lapsState,
            modifier = Modifier.fillMaxHeight().widthIn(max = this@BoxWithConstraints.maxWidth / 2),
          )

          Spacer(modifier = Modifier.width(32.dp))
        }
      }
    } else {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Spacer(modifier = Modifier.weight(1f))

        DurationText(
          duration = duration,
          layoutType = DurationTextLayoutType.Vertical,
          modifier = Modifier.padding(vertical = 16.dp),
        )

        if (laps.isNotEmpty()) {
          val lapsState = rememberLazyListState()
          LaunchedEffect(laps.size) { lapsState.animateScrollToItem(laps.lastIndex) }

          Laps(
            laps = laps,
            state = lapsState,
            modifier = Modifier.heightIn(max = this@BoxWithConstraints.maxHeight / 2),
          )
        }

        Spacer(modifier = Modifier.weight(1f))

        StopwatchDurationControls(
          state = state,
          onStartStopClick = onStartStopClick,
          onCancelClick = onCancelClick,
          onRecordLapClick = onRecordLapClick,
        )

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun Laps(
  laps: SnapshotStateList<Duration>,
  state: LazyListState = rememberLazyListState(),
  modifier: Modifier = Modifier,
) {
  LazyColumn(contentPadding = PaddingValues(16.dp), state = state, modifier = modifier) {
    itemsIndexed(laps) { lapIndex, lapEndDuration ->
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        val (lapTime, lapFractionOfSecond) =
          rememberDurationText(lapEndDuration - (laps.getOrElse(lapIndex - 1) { Duration.ZERO }))
        Text(
          text = "$lapTime.$lapFractionOfSecond",
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
        )

        Spacer(modifier = Modifier.width(16.dp))

        val (lapEndTime, lapEndFractionOfSecond) = rememberDurationText(lapEndDuration)
        Text(
          text = "$lapEndTime.$lapEndFractionOfSecond",
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
        )
      }
    }
  }
}

@Composable
private fun StopwatchDurationControls(
  state: StopwatchState,
  onStartStopClick: () -> Unit,
  onCancelClick: () -> Unit,
  onRecordLapClick: () -> Unit,
) {
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

    AnimatedContent(
      targetState = state == StopwatchState.RUNNING,
      transitionSpec =
        AnimatedContentTransitionScope<Boolean>::sideFloatingActionButtonTransitionSpec,
    ) {
      if (it) {
        FloatingActionButton(
          onClick = onRecordLapClick,
          elevation = FloatingActionButtonDefaults.loweredElevation(),
        ) {
          Icon(imageVector = Icons.Default.Timer, contentDescription = "Record lap")
        }
      } else {
        FloatingActionButtonSpacerBox()
      }
    }
  }
}

@Composable
private fun FloatingActionButtonSpacerBox() {
  Box(
    modifier =
      Modifier.size(56.dp).clip(FloatingActionButtonDefaults.shape).background(Color.Transparent)
  )
}
