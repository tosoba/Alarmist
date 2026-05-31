package com.trm.alarmist.core.common.util

import android.content.Intent
import android.os.Parcelable
import androidx.core.content.IntentCompat
import com.trm.alarmist.core.domain.model.AlarmFireSettings
import java.io.Serializable

inline fun <reified T : Serializable> Intent.getSerializable(key: String): T? =
  IntentCompat.getSerializableExtra(this, key, T::class.java)

inline fun <reified T : Parcelable> Intent.getParcelable(key: String?): T? =
  IntentCompat.getParcelableExtra(this, key, T::class.java)

fun Intent.requireAlarmFireSettings(): AlarmFireSettings =
  requireNotNull(getParcelable(EXTRA_ALARM_FIRE_SETTINGS))

const val EXTRA_ALARM_FIRE_SETTINGS = "ALARM_FIRE_SETTINGS"
