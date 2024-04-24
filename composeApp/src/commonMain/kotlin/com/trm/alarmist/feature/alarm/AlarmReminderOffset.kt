package com.trm.alarmist.feature.alarm

enum class AlarmReminderOffset(val hours: Long) {
  HOUR_1(1L),
  HOUR_2(2L),
  HOUR_4(4L),
  HOUR_6(6L),
  HOUR_12(12L),
  HOUR_24(24L);

  companion object {
    fun fromHours(hours: Long): AlarmReminderOffset =
      AlarmReminderOffset.entries.first { it.hours == hours }
  }
}
