package com.trm.alarmist.feature.alarm.sound

import android.media.RingtoneManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberAlarmSounds(ringtoneManager: RingtoneManager): List<AndroidAlarmSound> = remember {
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
