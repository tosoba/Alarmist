package com.trm.alarmist.feature.stopwatch

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect
import com.trm.alarmist.core.common.util.zeroPadded
import com.trm.alarmist.core.domain.model.StopwatchState
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.system.stopwatch.StopwatchService
import kotlin.time.Duration

@Composable
actual fun StopwatchContent(modifier: Modifier, component: StopwatchComponent) {
  val handler = postNotificationsPermissionHandler {}
  LaunchedEffect(Unit) { handler() }

  val context = LocalContext.current

  var service: StopwatchService? by remember { mutableStateOf(null) }
  val stopwatchState by remember { derivedStateOf { service?.state ?: StopwatchState.IDLE } }
  val stopwatchDuration by remember {
    derivedStateOf {
      (service?.duration ?: Duration.ZERO).toComponents { hours, minutes, seconds, _ ->
        Triple(hours.toInt().zeroPadded(), minutes.zeroPadded(), seconds.zeroPadded())
      }
    }
  }

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

  val (hours, minutes, seconds) = stopwatchDuration
  StopwatchTime(
    hours = hours,
    minutes = minutes,
    seconds = seconds,
    state = stopwatchState,
    onStartStopClick = {
      StopwatchService.start(
        context = context,
        action =
          if (stopwatchState == StopwatchState.STARTED) StopwatchService.Action.STOP
          else StopwatchService.Action.START,
      )
    },
    onCancelClick = {
      StopwatchService.start(context = context, action = StopwatchService.Action.CANCEL)
    },
  )
}
