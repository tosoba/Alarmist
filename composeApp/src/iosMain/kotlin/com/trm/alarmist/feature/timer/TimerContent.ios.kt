package com.trm.alarmist.feature.timer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.trm.alarmist.core.domain.model.TimerState
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.system.timer.IosTimerController
import org.koin.compose.koinInject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
actual fun TimerContent(modifier: Modifier, component: TimerComponent) {
  val permissionHandler = postNotificationsPermissionHandler {}
  LaunchedEffect(Unit) { permissionHandler() }

  val controller = koinInject<IosTimerController>()

  val state by controller.state.collectAsState()
  val duration by controller.duration.collectAsState()
  val initialDuration by controller.initialDuration.collectAsState()

  LaunchedEffect(Unit) {
    // If we return to the app after being suspended, recompute remaining.
    controller.refreshFromClock(playElapsedInForeground = false)
  }

  DisposableEffect(component.lifecycle) {
    val callbacks =
      object : Lifecycle.Callbacks {
        override fun onResume() {
          controller.onAppForegrounded()
        }

        override fun onPause() {
          controller.onAppBackgrounded()
        }
      }

    component.lifecycle.subscribe(callbacks)
    onDispose { component.lifecycle.unsubscribe(callbacks) }
  }

  TimerScaffold(
    modifier = modifier,
    duration = duration,
    initialDuration = initialDuration,
    state = state,
    onStartClick = { newDuration -> controller.start(newDuration) },
    onToggleRunningClick = { controller.toggleRunning() },
    onCancelClick = { controller.cancel() },
    onResetClick = { controller.reset() },
    onAddMinuteClick = { controller.addDuration(1.minutes) },
    onSubtractMinuteClick = { controller.subtractDuration(1.minutes) },
  )
}

@Composable
private fun TimerScaffold(
  duration: Duration,
  initialDuration: Duration,
  state: TimerState,
  onStartClick: (Duration) -> Unit,
  onToggleRunningClick: () -> Unit,
  onCancelClick: () -> Unit,
  onResetClick: () -> Unit,
  onAddMinuteClick: () -> Unit,
  onSubtractMinuteClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(modifier = modifier) {
    if (state == TimerState.IDLE) {
      TimerInput(onStartClick = onStartClick, modifier = Modifier.fillMaxSize().padding(it))
    } else {
      TimerDuration(
        modifier = Modifier.fillMaxSize().padding(it),
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
