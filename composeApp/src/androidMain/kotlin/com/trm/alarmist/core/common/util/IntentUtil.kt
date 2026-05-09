package com.trm.alarmist.core.common.util

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import com.trm.alarmist.core.common.domain.model.AlarmFireSettings
import java.io.Serializable

inline fun <reified T : Serializable> Intent.getSerializable(name: String): T? =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getSerializableExtra(name, T::class.java)
  } else {
    getSerializableExtra(name) as? T
  }

inline fun <reified T : Parcelable> Intent.getParcelable(name: String?): T? =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getParcelableExtra(name, T::class.java)
  } else {
    getParcelableExtra(name)
  }

fun Intent.requireAlarmFireSettings(): AlarmFireSettings =
  requireNotNull(getParcelable(EXTRA_ALARM_FIRE_SETTINGS))

const val EXTRA_ALARM_FIRE_SETTINGS = "ALARM_FIRE_SETTINGS"
