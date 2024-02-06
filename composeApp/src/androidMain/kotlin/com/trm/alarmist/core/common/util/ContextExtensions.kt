package com.trm.alarmist.core.common.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

@Composable
fun Context.requestPermission(permission: String, onDenied: () -> Unit, onGranted: () -> Unit) {
  val permissionCheckResult = ContextCompat.checkSelfPermission(this, permission)
  if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
    onGranted()
  } else {
    val launcher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          onGranted()
        } else {
          onDenied()
        }
      }
    launcher.launch(permission)
  }
}
