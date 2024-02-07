package com.trm.alarmist.feature.alarm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.trm.alarmist.core.ui.WheelTimePicker
import kotlinx.datetime.LocalTime

@Composable
fun AlarmContent(
  modifier: Modifier = Modifier,
  state: AlarmState,
  onFireAtChange: (LocalTime) -> Unit,
  onConfirmClick: () -> Unit,
) {
  Box(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
      val textStyle = MaterialTheme.typography.headlineMedium
      val textHeightDp = with(LocalDensity.current) { textStyle.fontSize.toDp() } + 10.dp
      WheelTimePicker(
        startTime = state.fireAt,
        rowCount = 5,
        size = DpSize(textHeightDp, textHeightDp) * 5,
        textStyle = textStyle,
        centerTextStyle = textStyle.copy(fontWeight = FontWeight.Bold),
        onSnappedTime = onFireAtChange,
      )
    }

    var permissionDialogVisible by rememberSaveable { mutableStateOf(false) }
    var shouldShowRationale by rememberSaveable { mutableStateOf(false) }

    val permissionsHandler =
      alarmPermissionsHandler(
        onDenied = {
          shouldShowRationale = it
          permissionDialogVisible = true
        },
        onGranted = onConfirmClick,
      )

    PostNotificationPermissionInfoDialog(
      visible = permissionDialogVisible,
      text =
        if (shouldShowRationale) {
          "A permission to post notifications is required to create an alarm. Permission dialog will appear again after clicking OK."
        } else {
          "A permission to post notifications which you have denied is required to create an alarm. Use application settings to grant that permission."
        },
      onDismiss = { permissionDialogVisible = false },
      onOkClick = {
        permissionDialogVisible = false
        if (shouldShowRationale) {
          permissionsHandler()
        }
      },
    )

    FloatingActionButton(
      modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
      onClick = permissionsHandler,
    ) {
      Icon(imageVector = Icons.Default.Check, contentDescription = "Confirm")
    }
  }
}

@Composable
private fun PostNotificationPermissionInfoDialog(
  modifier: Modifier = Modifier,
  visible: Boolean,
  text: String,
  onOkClick: () -> Unit,
  onDismiss: () -> Unit,
) {
  AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
    AlertDialog(
      modifier = modifier,
      onDismissRequest = onDismiss,
      confirmButton = { TextButton(onClick = onOkClick) { Text(text = "OK") } },
      dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancel") } },
      title = { Text(text = "Permission required", textAlign = TextAlign.Center) },
      text = { Text(text = text) },
    )
  }
}
