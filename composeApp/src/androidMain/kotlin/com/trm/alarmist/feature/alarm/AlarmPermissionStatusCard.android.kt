package com.trm.alarmist.feature.alarm

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarm_notifications_disabled_description
import alarmist.composeapp.generated.resources.alarm_notifications_disabled_title
import alarmist.composeapp.generated.resources.settings
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun AlarmPermissionStatusCard(modifier: Modifier) {
  val context = LocalContext.current
  val settingsLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

  ElevatedCard(
    modifier = modifier,
    colors =
      CardColors(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
        disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
      ),
  ) {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(Res.string.alarm_notifications_disabled_title),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(stringResource(Res.string.alarm_notifications_disabled_description))
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
