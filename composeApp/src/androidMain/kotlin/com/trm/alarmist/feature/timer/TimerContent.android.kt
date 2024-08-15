package com.trm.alarmist.feature.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
actual fun TimerContent(component: TimerComponent, modifier: Modifier) {
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

  TimerScaffold(
    duration = service?.duration ?: Duration.ZERO,
    initialDuration = service?.initialDuration ?: Duration.ZERO,
    state = service?.state ?: TimerState.IDLE,
    onStartClick = { initialDuration ->
      if (initialDuration.inWholeSeconds > 0L) {
        TimerService.startWithAction(
          context = context,
          action = TimerService.Action.Start(initialDuration),
        )
      }
    },
    onToggleRunningClick = {
      TimerService.startWithAction(context = context, action = TimerService.Action.ToggleRunning)
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
