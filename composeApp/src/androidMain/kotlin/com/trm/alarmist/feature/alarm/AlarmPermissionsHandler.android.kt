package com.trm.alarmist.feature.alarm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.trm.alarmist.core.common.util.getActivity

@Composable
actual fun alarmPermissionsHandler(
  onDenied: (shouldShowRationale: Boolean) -> Unit,
  onGranted: () -> Unit,
): () -> Unit {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    val context = LocalContext.current
    val launcher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          onGranted()
        } else {
          onDenied(
            ActivityCompat.shouldShowRequestPermissionRationale(
              requireNotNull(context.getActivity()),
              Manifest.permission.POST_NOTIFICATIONS,
            )
          )
        }
      }
    return {
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
    return onGranted
  }
}
