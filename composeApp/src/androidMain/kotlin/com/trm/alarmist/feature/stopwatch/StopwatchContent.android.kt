package com.trm.alarmist.feature.stopwatch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.trm.alarmist.core.system.permission.postNotificationsPermissionHandler

@Composable
actual fun StopwatchContent(modifier: Modifier, component: StopwatchComponent) {
  val handler = postNotificationsPermissionHandler {}
  LaunchedEffect(Unit) { handler() }
}
