package com.trm.alarmist.feature.stopwatch

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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

  Scaffold(modifier = modifier) {
    StopwatchDuration(
      modifier =
        Modifier.fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(bottom = it.calculateBottomPadding()),
      duration = duration,
      state = state,
      onStartStopClick = {
        StopwatchService.startWithAction(
          context = context,
          action = StopwatchService.Action.TOGGLE_RUNNING,
        )
      },
      onCancelClick = {
        StopwatchService.startWithAction(context = context, action = StopwatchService.Action.CANCEL)
      },
    )
  }
}
