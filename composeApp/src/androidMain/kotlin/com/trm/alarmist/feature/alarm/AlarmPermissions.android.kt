package com.trm.alarmist.feature.alarm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun alarmPermissionsHandler(onDenied: (String) -> Unit, onGranted: () -> Unit): () -> Unit =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    val launcher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          onGranted()
        } else {
          onDenied(Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    val context = LocalContext.current
    {
      if (
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED
      ) {
        onGranted()
      } else {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  } else {
    onGranted
  }
