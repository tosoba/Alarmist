package com.trm.alarmist.feature.alarm

enum class AlarmSnoozeDuration(val minutes: Int) {
  ZERO(0),
  MIN_1(1),
  MIN_2(2),
  MIN_5(5),
  MIN_10(10),
  MIN_15(15),
  MIN_30(30),
  H_1(60)
}
