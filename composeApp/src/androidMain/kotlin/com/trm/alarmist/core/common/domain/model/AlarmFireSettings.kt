package com.trm.alarmist.core.common.domain.model

import android.os.Parcelable
import com.trm.alarmist.core.common.util.LocalDateTimeParceler
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
data class AlarmFireSettings(
  val id: Long,
  val name: String?,
  @TypeParceler<LocalDateTime, LocalDateTimeParceler> val fireOnDateTime: LocalDateTime,
  val snoozeAvailable: Boolean,
  val alarmDurationMinutes: Long,
  val soundEnabled: Boolean,
  val soundId: String?,
  val vibrationEnabled: Boolean,
) : Parcelable
