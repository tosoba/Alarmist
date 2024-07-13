package com.trm.alarmist.feature.sheet

import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.group.GroupComponent

sealed interface BottomSheetChild {
  class Alarm(val component: AlarmComponent) : BottomSheetChild

  class Group(val component: GroupComponent) : BottomSheetChild
}
