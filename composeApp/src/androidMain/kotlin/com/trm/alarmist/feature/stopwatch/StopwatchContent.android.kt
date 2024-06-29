package com.trm.alarmist.feature.stopwatch

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect
import com.trm.alarmist.core.domain.model.StopwatchState
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.system.stopwatch.StopwatchService

@Composable
actual fun StopwatchContent(modifier: Modifier, component: StopwatchComponent) {
  val handler = postNotificationsPermissionHandler {}
  LaunchedEffect(Unit) { handler() }

  var service: StopwatchService? by remember { mutableStateOf(null) }
  val context = LocalContext.current

  LifecycleStartEffect(Unit) {
    val connection =
      object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
          service = (binder as StopwatchService.StopwatchBinder).getService()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
          service = null
        }
      }

    context.bindService(
      Intent(context, StopwatchService::class.java),
      connection,
      Context.BIND_AUTO_CREATE,
    )

    onStopOrDispose { context.unbindService(connection) }
  }

  AnimatedVisibility(visible = service != null) {
    service?.let {
      StopwatchTime(
        hours = it.hours.value,
        minutes = it.minutes.value,
        seconds = it.seconds.value,
        state = it.currentState.value,
        onStartStopClick = {
          StopwatchService.start(
            context = context,
            action =
              if (it.currentState.value == StopwatchState.Started) StopwatchService.Action.STOP
              else StopwatchService.Action.START,
          )
        },
        onCancelClick = {
          StopwatchService.start(context = context, action = StopwatchService.Action.CANCEL)
        },
      )
    }
  }
}
