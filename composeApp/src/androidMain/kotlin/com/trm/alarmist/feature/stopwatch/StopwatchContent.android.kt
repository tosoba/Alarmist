package com.trm.alarmist.feature.stopwatch

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler
import com.trm.alarmist.core.system.stopwatch.Constants
import com.trm.alarmist.core.system.stopwatch.ServiceHelper
import com.trm.alarmist.core.system.stopwatch.StopwatchService
import com.trm.alarmist.core.system.stopwatch.StopwatchState

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
      StopwatchServiceContent(
        hours = it.hours.value,
        minutes = it.minutes.value,
        seconds = it.seconds.value,
        currentState = it.currentState.value,
      )
    }
  }
}

@Composable
fun StopwatchServiceContent(
  hours: String,
  minutes: String,
  seconds: String,
  currentState: StopwatchState,
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(30.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Column(
      modifier = Modifier.weight(weight = 9f),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      AnimatedContent(targetState = hours, label = "AnimatedContent-Hours") {
        Text(
          text = it,
          style =
            TextStyle(
              fontSize = MaterialTheme.typography.headlineLarge.fontSize,
              fontWeight = FontWeight.Bold,
              color = if (hours == "00") Color.White else Color.Blue,
            ),
        )
      }
      AnimatedContent(targetState = minutes, label = "AnimatedContent-Minutes") {
        Text(
          text = it,
          style =
            TextStyle(
              fontSize = MaterialTheme.typography.headlineLarge.fontSize,
              fontWeight = FontWeight.Bold,
              color = if (minutes == "00") Color.White else Color.Blue,
            ),
        )
      }
      AnimatedContent(targetState = seconds, label = "AnimatedContent-Seconds") {
        Text(
          text = it,
          style =
            TextStyle(
              fontSize = MaterialTheme.typography.headlineLarge.fontSize,
              fontWeight = FontWeight.Bold,
              color = if (seconds == "00") Color.White else Color.Blue,
            ),
        )
      }
    }
    Row(modifier = Modifier.weight(weight = 1f)) {
      Button(
        modifier = Modifier.weight(1f).fillMaxHeight(0.8f),
        onClick = {
          ServiceHelper.triggerForegroundService(
            context = context,
            action =
              if (currentState == StopwatchState.Started) Constants.ACTION_SERVICE_STOP
              else Constants.ACTION_SERVICE_START,
          )
        },
        colors =
          ButtonDefaults.buttonColors(
            containerColor = if (currentState == StopwatchState.Started) Color.Red else Color.Blue,
            contentColor = Color.White,
          ),
      ) {
        Text(
          text =
            when (currentState) {
              StopwatchState.Started -> "Stop"
              StopwatchState.Stopped -> "Resume"
              else -> "Start"
            }
        )
      }
      Spacer(modifier = Modifier.width(30.dp))
      Button(
        modifier = Modifier.weight(1f).fillMaxHeight(0.8f),
        onClick = {
          ServiceHelper.triggerForegroundService(
            context = context,
            action = Constants.ACTION_SERVICE_CANCEL,
          )
        },
        enabled = seconds != "00" && currentState != StopwatchState.Started,
        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF17162B)),
      ) {
        Text(text = "Cancel")
      }
    }
  }
}
