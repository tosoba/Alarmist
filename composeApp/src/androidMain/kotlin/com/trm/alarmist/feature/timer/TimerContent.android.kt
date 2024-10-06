package com.trm.alarmist.feature.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.trm.alarmist.core.domain.model.TimerState
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.system.timer.TimerService
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
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

  val state by
    rememberSaveable(service, saver = TimerStateSaver) {
      derivedStateOf { service?.state ?: TimerState.IDLE }
    }
  val duration by
    rememberSaveable(service, saver = DurationSaver) {
      derivedStateOf { service?.duration ?: Duration.ZERO }
    }
  val initialDuration by
    rememberSaveable(service, saver = DurationSaver) {
      derivedStateOf { service?.initialDuration ?: Duration.ZERO }
    }

  DisposableEffect(component.lifecycle) {
    val callbacks =
      component.lifecycle.subscribe(
        onResume = {
          TimerService.startWithAction(
            context = context,
            action = TimerService.Action.HideNotification,
          )
        },
        onPause = {
          if (state != TimerState.IDLE) {
            TimerService.startWithAction(
              context = context,
              action = TimerService.Action.ShowNotification,
            )
          }
        },
      )
    onDispose { component.lifecycle.unsubscribe(callbacks) }
  }

  TimerScaffold(
    duration = service?.duration ?: duration,
    initialDuration = service?.initialDuration ?: initialDuration,
    state = service?.state ?: state,
    onStartClick = { newDuration ->
      if (newDuration.inWholeSeconds > 0L) {
        TimerService.startWithAction(
          context = context,
          action = TimerService.Action.Start(newDuration),
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

private object DurationSaver : Saver<State<Duration>, Long> {
  override fun SaverScope.save(value: State<Duration>): Long = value.value.inWholeMilliseconds

  override fun restore(value: Long): State<Duration> = mutableStateOf(value.milliseconds)
}

private object TimerStateSaver : Saver<State<TimerState>, String> {
  override fun SaverScope.save(value: State<TimerState>): String = value.value.name

  override fun restore(value: String): State<TimerState> = mutableStateOf(TimerState.valueOf(value))
}
