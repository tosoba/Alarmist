package com.trm.alarmist.feature.stopwatch

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
import com.trm.alarmist.core.domain.model.StopwatchState
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.system.stopwatch.StopwatchService
import kotlin.time.Duration

@Composable
actual fun StopwatchContent(modifier: Modifier, component: StopwatchComponent) {
  val permissionHandler = postNotificationsPermissionHandler {}
  LaunchedEffect(Unit) { permissionHandler() }

  val context = LocalContext.current
  var service: StopwatchService? by remember { mutableStateOf(null) }

  DisposableEffect(Unit) {
    val connection =
      object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
          service = (binder as StopwatchService.StopwatchBinder).getService()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
          service = null
        }
      }

    val bound =
      context.bindService(
        Intent(context, StopwatchService::class.java),
        connection,
        Context.BIND_AUTO_CREATE,
      )

    onDispose { if (bound) context.unbindService(connection) }
  }

  val state by remember { derivedStateOf { service?.state ?: StopwatchState.IDLE } }
  val duration by remember { derivedStateOf { service?.duration ?: Duration.ZERO } }

  StopwatchDuration(
    duration = duration,
    state = state,
    onStartStopClick = {
      StopwatchService.start(
        context = context,
        action =
          if (state == StopwatchState.STARTED) StopwatchService.Action.STOP
          else StopwatchService.Action.START,
      )
    },
    onCancelClick = {
      StopwatchService.start(context = context, action = StopwatchService.Action.CANCEL)
    },
  )
}
