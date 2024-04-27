package com.trm.alarmist.feature.alarm.sound

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.default
import android.media.RingtoneManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun alarmSoundTitle(id: String?): String {
  val context = LocalContext.current
  val ringtoneManager = remember {
    RingtoneManager(context).apply { setType(RingtoneManager.TYPE_ALARM) }
  }
  val sounds = rememberAlarmSounds(ringtoneManager)
  return if (id == null) {
    RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
      ?.let { defaultSoundUri -> sounds.find { sound -> sound.uri == defaultSoundUri } }
      ?.title
  } else {
    sounds.find { it.id == id }?.title
  } ?: stringResource(Res.string.default)
}

