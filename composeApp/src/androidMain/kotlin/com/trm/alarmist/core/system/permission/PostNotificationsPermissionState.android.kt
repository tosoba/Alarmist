package com.trm.alarmist.core.system.permission

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import dev.shreyaspatil.permissionflow.compose.rememberPermissionState

@Composable
actual fun isPostNotificationPermissionGranted(): Boolean {
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
  val state by rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
  return state.isGranted
}
