package com.trm.alarmist.core.system

import android.content.Intent
import android.os.Build

internal const val EXTRA_ALARM_FIRE_SETTINGS = "ALARM_FIRE_SETTINGS"

internal fun getAlarmFireSettings(intent: Intent): AlarmFireSettings =
  requireNotNull(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      intent.getParcelableExtra(EXTRA_ALARM_FIRE_SETTINGS, AlarmFireSettings::class.java)
    } else {
      intent.getParcelableExtra(EXTRA_ALARM_FIRE_SETTINGS)
    }
  )
