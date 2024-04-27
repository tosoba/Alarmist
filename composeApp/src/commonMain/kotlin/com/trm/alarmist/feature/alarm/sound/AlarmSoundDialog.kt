package com.trm.alarmist.feature.alarm.sound

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.cancel
import alarmist.composeapp.generated.resources.confirm
import alarmist.composeapp.generated.resources.pause
import alarmist.composeapp.generated.resources.play
import alarmist.composeapp.generated.resources.sound_label
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AlarmSoundDialog(component: AlarmSoundDialogComponent, modifier: Modifier = Modifier) {
  AlertDialog(
    onDismissRequest = component.onDismiss,
    title = { Text(text = stringResource(Res.string.sound_label)) },
    text = {
      AlarmSoundLazyColumn(
        selectedId = component.selectedSoundId,
        onSoundSelected = component.onSoundSelected,
        modifier = Modifier.fillMaxWidth(),
      )
    },
    confirmButton = {
      TextButton(onClick = component.onDismiss) { Text(stringResource(Res.string.confirm)) }
    },
    modifier = modifier,
  )
}

@Composable
expect fun AlarmSoundLazyColumn(
  selectedId: String?,
  onSoundSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
)

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AlarmSoundItem(
  title: String,
  isSelected: Boolean,
  isPlaying: Boolean,
  onClick: () -> Unit,
  onTogglePlayClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    selected = isSelected,
    onClick = onClick,
    modifier = modifier,
    color = NavigationDrawerItemDefaults.colors().containerColor(isSelected).value,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(start = 16.dp),
    ) {
      Text(
        text = title,
        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
        modifier = Modifier.weight(1f),
      )

      IconButton(onClick = onTogglePlayClick) {
        Crossfade(isPlaying) {
          if (it) {
            Icon(
              imageVector = Icons.Default.Pause,
              contentDescription = stringResource(Res.string.pause),
            )
          } else {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = stringResource(Res.string.play),
            )
          }
        }
      }
    }
  }
}
