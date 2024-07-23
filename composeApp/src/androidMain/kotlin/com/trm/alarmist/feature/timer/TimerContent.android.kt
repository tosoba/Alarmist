package com.trm.alarmist.feature.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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

  // TODO: show duration only for start/stop TimerState
  // TODO: for idle show duration input keyboard - UI should be in common
  // TODO: for elapsed show elapsed at info and reset button and back to idle keyboard button - UI
  when (state) {
    TimerState.IDLE -> {
      TimerInput()
    }
    TimerState.STARTED,
    TimerState.STOPPED,
    TimerState.ELAPSED -> {
      TimerDuration(
        duration = duration,
        state = state,
        onStartStopClick = {
          TimerService.startWithAction(
            context = context,
            action =
              when (state) {
                TimerState.STARTED -> TimerService.Action.Stop
                TimerState.STOPPED -> TimerService.Action.Resume
                else -> TimerService.Action.Start(1.minutes)
              },
          )
        },
        onCancelClick = {
          TimerService.startWithAction(context = context, action = TimerService.Action.Cancel)
        },
      )
    }
  }
}
