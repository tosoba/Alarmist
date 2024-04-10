package com.trm.alarmist.feature.alarm

enum class AlarmSnoozeDuration(val minutes: Long) {
  ZERO(0L),
  MIN_1(1L),
  MIN_2(2L),
  MIN_5(5L),
  MIN_10(10L),
  MIN_15(15L),
  MIN_20(20L),
  MIN_30(30L),
  H_1(60L);

  companion object {
    fun fromMinutes(minutes: Long): AlarmSnoozeDuration =
      AlarmSnoozeDuration.entries.first { it.minutes == minutes }
  }
}
