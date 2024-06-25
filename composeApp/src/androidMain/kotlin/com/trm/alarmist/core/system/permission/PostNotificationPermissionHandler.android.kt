package com.trm.alarmist.core.system.permission

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.cancel
import alarmist.composeapp.generated.resources.notification_permission_rationale
import alarmist.composeapp.generated.resources.notification_permission_settings
import alarmist.composeapp.generated.resources.ok
import alarmist.composeapp.generated.resources.permission_required
import alarmist.composeapp.generated.resources.settings
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.trm.alarmist.core.common.util.getActivity
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun postNotificationsPermissionHandler(onGranted: () -> Unit): () -> Unit {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    val context = LocalContext.current

    var permissionDialogVisible by rememberSaveable { mutableStateOf(false) }
    var shouldShowRationale by rememberSaveable { mutableStateOf(false) }

    val requestPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          onGranted()
        } else {
          shouldShowRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
              requireNotNull(context.getActivity()),
              Manifest.permission.POST_NOTIFICATIONS,
            )
          permissionDialogVisible = true
        }
      }

    val settingsLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        if (
          ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
          onGranted()
        }
      }

    PostNotificationPermissionInfoDialog(
      visible = permissionDialogVisible,
      text =
        stringResource(
          if (shouldShowRationale) Res.string.notification_permission_rationale
          else Res.string.notification_permission_settings
        ),
      confirmText = stringResource(if (shouldShowRationale) Res.string.ok else Res.string.settings),
      onDismiss = { permissionDialogVisible = false },
      onConfirmClick = {
        permissionDialogVisible = false
        if (shouldShowRationale) {
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
          settingsLauncher.launch(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
              .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
          )
        }
      },
    )

    return {
      if (
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED
      ) {
        onGranted()
      } else {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  } else {
    return onGranted
  }
}

@Composable
private fun PostNotificationPermissionInfoDialog(
  modifier: Modifier = Modifier,
  visible: Boolean,
  text: String,
  confirmText: String,
  onConfirmClick: () -> Unit,
  onDismiss: () -> Unit,
) {
  AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
    AlertDialog(
      modifier = modifier,
      onDismissRequest = onDismiss,
      confirmButton = { TextButton(onClick = onConfirmClick) { Text(text = confirmText) } },
      dismissButton = {
        TextButton(onClick = onDismiss) { Text(text = stringResource(Res.string.cancel)) }
      },
      title = {
        Text(text = stringResource(Res.string.permission_required), textAlign = TextAlign.Center)
      },
      text = { Text(text = text) },
    )
  }
}
