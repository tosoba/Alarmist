package com.trm.alarmist.core.common.util

import android.os.Parcel
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parceler

object LocalDateTimeParceler : Parceler<LocalDateTime> {
  override fun create(parcel: Parcel): LocalDateTime =
    parcel.readString()?.let(LocalDateTime.Companion::parse) ?: LocalDateTime(0, 0, 0, 0, 0)

  override fun LocalDateTime.write(parcel: Parcel, flags: Int) {
    parcel.writeString(toString())
  }
}