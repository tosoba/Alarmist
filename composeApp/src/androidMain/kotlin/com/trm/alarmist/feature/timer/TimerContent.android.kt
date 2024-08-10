package com.trm.alarmist.feature.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.trm.alarmist.core.domain.model.TimerState
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.system.timer.TimerService
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
actual fun TimerContent(modifier: Modifier, component: TimerComponent) {
  val permissionHandler = postNotificationsPermissionHandler {}
  LaunchedEffect(Unit) { permissionHandler() }

  val context = LocalContext.current
  var service: TimerService? by remember { mutableStateOf(null) }

  DisposableEffect(Unit) {
    val connection =
      object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
          service = (binder as TimerService.TimerBinder).getService()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
          service = null
        }
      }

    val bound =
      context.bindService(
        Intent(context, TimerService::class.java),
        connection,
        Context.BIND_AUTO_CREATE,
      )

    onDispose { if (bound) context.unbindService(connection) }
  }

  val state by remember { derivedStateOf { service?.state ?: TimerState.IDLE } }
  val duration by remember { derivedStateOf { service?.duration ?: Duration.ZERO } }
  val initialDuration by remember { derivedStateOf { service?.initialDuration ?: Duration.ZERO } }

  Scaffold(modifier = modifier) { padding ->
    Crossfade(targetState = state == TimerState.IDLE, label = "TimerContent") {
      if (it) {
        TimerInput(
          modifier =
            Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .padding(bottom = padding.calculateBottomPadding()),
          onStartClick = { initialDuration ->
            if (initialDuration.inWholeSeconds > 0L) {
              TimerService.startWithAction(
                context = context,
                action = TimerService.Action.Start(initialDuration),
              )
            }
          },
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
          onToggleRunningClick = {
            TimerService.startWithAction(
              context = context,
              action = TimerService.Action.ToggleRunning,
            )
          },
          onCancelClick = {
            TimerService.startWithAction(context = context, action = TimerService.Action.Cancel)
          },
          onResetClick = {
            TimerService.startWithAction(context = context, action = TimerService.Action.Reset)
          },
          onAddMinuteClick = {
            TimerService.startWithAction(
              context = context,
              action = TimerService.Action.AddDuration(1.minutes),
            )
          },
          onSubtractMinuteClick = {
            TimerService.startWithAction(
              context = context,
              action = TimerService.Action.SubtractDuration(1.minutes),
            )
          },
        )
      }
    }
  }
}
