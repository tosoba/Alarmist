package com.trm.alarmist.feature.stopwatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.system.stopwatch.IosStopwatchEnvironment
import kotlin.time.Duration

@Composable
actual fun StopwatchContent(modifier: Modifier, component: StopwatchComponent) {
  val permissionHandler = postNotificationsPermissionHandler {}
  LaunchedEffect(Unit) { permissionHandler() }

  val controller = IosStopwatchEnvironment.controller

  val state by controller.state.collectAsState()
  val duration by controller.duration.collectAsState()
  val laps by controller.laps.collectAsState()
  val lapsState = remember { mutableStateListOf<Duration>() }

  LaunchedEffect(Unit) { controller.refreshFromClock() }
  LaunchedEffect(laps) {
    lapsState.clear()
    lapsState.addAll(laps)
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

  Scaffold(modifier = modifier) {
    StopwatchDuration(
      modifier =
        Modifier.fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(bottom = it.calculateBottomPadding()),
      duration = duration,
      state = state,
      laps = lapsState,
      onStartStopClick = { controller.toggleRunning() },
      onCancelClick = { controller.cancel() },
      onRecordLapClick = { controller.recordLap() },
    )
  }
}
