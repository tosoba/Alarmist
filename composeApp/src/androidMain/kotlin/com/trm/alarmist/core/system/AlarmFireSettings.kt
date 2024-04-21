package com.trm.alarmist.core.system

import android.os.Parcelable
import com.trm.alarmist.core.common.util.LocalDateTimeParceler
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
data class AlarmFireSettings(
  val id: Long,
  @TypeParceler<LocalDateTime, LocalDateTimeParceler> val fireOnDateTime: LocalDateTime,
  val snoozeAvailable: Boolean,
  val ringDurationMinutes: Long,
  val soundEnabled: Boolean,
  val vibrationEnabled: Boolean,
) : Parcelable
