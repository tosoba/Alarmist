package com.trm.alarmist.feature.alarm.sound

import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun AlarmSoundLazyColumn(modifier: Modifier) {
  val context = LocalContext.current

  val ringtoneManager = remember {
    RingtoneManager(context).apply { setType(RingtoneManager.TYPE_ALARM) }
  }

  DisposableEffect(Unit) { onDispose { ringtoneManager.stopPreviousRingtone() } }

  val alarmSounds = remember {
    val alarms = mutableListOf<AndroidAlarmSound>()
    val cursor = ringtoneManager.cursor
    while (cursor.moveToNext()) {
      alarms.add(
        AndroidAlarmSound(
          id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX),
          title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),
          uri = ringtoneManager.getRingtoneUri(cursor.position),
        )
      )
    }
    alarms
  }

  LazyColumn(modifier = modifier) {
    itemsIndexed(alarmSounds) { index, alarm ->
      AlarmSoundItem(
        title = alarm.title,
        onPlayClick = {
          ringtoneManager.stopPreviousRingtone()
          ringtoneManager.getRingtone(index).play()
        },
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

data class AndroidAlarmSound(val id: String, val title: String, val uri: Uri)
