package com.trm.alarmist.feature.alarm.sound

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.confirm
import alarmist.composeapp.generated.resources.dismiss
import alarmist.composeapp.generated.resources.sound_label
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AlarmSoundDialog(component: AlarmSoundDialogComponent, modifier: Modifier = Modifier) {
  AlertDialog(
    onDismissRequest = component.onDismiss,
    title = { Text(text = stringResource(Res.string.sound_label)) },
    text = { AlarmSoundLazyColumn(modifier = Modifier.fillMaxWidth()) },
    confirmButton = {
      TextButton(onClick = component.onConfirm) { Text(stringResource(Res.string.confirm)) }
    },
    dismissButton = {
      TextButton(onClick = component.onDismiss) { Text(stringResource(Res.string.dismiss)) }
    },
    modifier = modifier,
  )
}

@Composable expect fun AlarmSoundLazyColumn(modifier: Modifier)

@Composable
fun AlarmSoundItem(title: String, onPlayClick: () -> Unit, modifier: Modifier = Modifier) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Text(text = title, modifier = Modifier.weight(1f))
    IconButton(onClick = onPlayClick) {
      Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
    }
  }
}
