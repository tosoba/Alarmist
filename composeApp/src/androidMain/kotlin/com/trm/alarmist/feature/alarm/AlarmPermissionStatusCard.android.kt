package com.trm.alarmist.feature.alarm

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarms_disabled_description
import alarmist.composeapp.generated.resources.alarms_disabled_title
import alarmist.composeapp.generated.resources.settings
import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.permissionflow.compose.rememberPermissionState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun AlarmPermissionStatusCard(modifier: Modifier) {
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

  val context = LocalContext.current
  val state by rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
  val settingsLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

  Crossfade(
    targetState = state.isGranted,
    modifier = modifier,
    label = "AlarmPermissionStatusCard-Crossfade",
  ) {
    if (it) {
      Spacer(modifier = Modifier.fillMaxWidth())
    } else {
      ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors =
          CardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
          ),
      ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = stringResource(Res.string.alarms_disabled_title),
              style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(Res.string.alarms_disabled_description))
          }

          TextButton(
            onClick = {
              settingsLauncher.launch(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                  .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
              )
            }
          ) {
            Text(stringResource(Res.string.settings))
          }
        }
      }
    }
  }
}
