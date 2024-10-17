package com.trm.alarmist.feature.alarm

import com.trm.alarmist.feature.alarm.sound.AlarmSoundDialogComponent
import com.trm.alarmist.feature.alarm.time.AlarmTimeDialogComponent

sealed interface AlarmDialogChild {
  data class Sound(val component: AlarmSoundDialogComponent) : AlarmDialogChild

  data class Time(val component: AlarmTimeDialogComponent) : AlarmDialogChild
}
