package com.trm.alarmist.feature.alarm.sound

import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
actual fun AlarmSoundLazyColumn(
  selectedId: String?,
  onSoundSelected: (String, String) -> Unit,
  modifier: Modifier,
) {
  val context = LocalContext.current
  val ringtoneManager = remember {
    RingtoneManager(context).apply { setType(RingtoneManager.TYPE_ALARM) }
  }
  val sounds = remember {
    val cursor = ringtoneManager.cursor
    buildList {
      while (cursor.moveToNext()) {
        add(
          AndroidAlarmSound(
            id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX),
            title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),
            uri = ringtoneManager.getRingtoneUri(cursor.position),
          )
        )
      }
    }
  }

  DisposableEffect(Unit) { onDispose { ringtoneManager.stopPreviousRingtone() } }

  var selectedSoundId: String? by remember {
    mutableStateOf(
      selectedId
        ?: RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
          ?.let { defaultSoundUri -> sounds.find { sound -> sound.uri == defaultSoundUri } }
          ?.id
    )
  }
  var playingSoundId: String? by remember { mutableStateOf(null) }

  LazyColumn(modifier = modifier) {
    itemsIndexed(sounds) { index, sound ->
      AlarmSoundItem(
        title = sound.title,
        isSelected = sound.id == selectedSoundId,
        isPlaying = sound.id == playingSoundId,
        onClick = {
          selectedSoundId = sound.id
          onSoundSelected(sound.id, sound.title)
        },
        onTogglePlayClick = {
          ringtoneManager.stopPreviousRingtone()
          if (playingSoundId != sound.id) {
            playingSoundId = sound.id
            ringtoneManager.getRingtone(index).play()
          } else {
            playingSoundId = null
          }
        },
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
      )
    }
  }
}

data class AndroidAlarmSound(val id: String, val title: String, val uri: Uri)
