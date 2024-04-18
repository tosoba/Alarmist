package com.trm.alarmist.core.system

import android.content.Intent
import kotlinx.datetime.LocalDateTime

internal const val EXTRA_ALARM_ID = "ALARM_ID"
internal const val EXTRA_FIRE_ON_DATE_TIME = "FIRE_ON_DATE_TIME"
internal const val EXTRA_SNOOZE_AVAILABLE = "SNOOZE_AVAILABLE"
internal const val EXTRA_RING_DURATION_MINUTES = "RING_DURATION_MINUTES"
internal const val EXTRA_SOUND_ENABLED = "SOUND_ENABLED"
internal const val EXTRA_VIBRATION_ENABLED = "VIBRATION_ENABLED"

internal fun getAlarmId(intent: Intent): Long =
  intent.getLongExtra(EXTRA_ALARM_ID, -1).takeIf { it > -1 }
    ?: throw IllegalArgumentException("Missing required EXTRA_ALARM_ID.")

internal fun getAlarmFireOnDateTime(intent: Intent): LocalDateTime =
  LocalDateTime.parse(
    requireNotNull(intent.getStringExtra(EXTRA_FIRE_ON_DATE_TIME)) {
      "Missing required EXTRA_FIRE_ON_DATE."
    }
  )

internal fun isSnoozeAvailable(intent: Intent): Boolean =
  intent.getBooleanExtra(EXTRA_SNOOZE_AVAILABLE, false)

internal fun getRingDurationMinutes(intent: Intent): Long =
  intent.getLongExtra(EXTRA_RING_DURATION_MINUTES, 1L)

internal fun isSoundEnabled(intent: Intent): Boolean =
  intent.getBooleanExtra(EXTRA_SOUND_ENABLED, false)

internal fun isVibrationEnabled(intent: Intent): Boolean =
  intent.getBooleanExtra(EXTRA_VIBRATION_ENABLED, false)
