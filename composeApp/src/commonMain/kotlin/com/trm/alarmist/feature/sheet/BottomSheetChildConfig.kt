package com.trm.alarmist.feature.sheet

import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.group.GroupComponent
import kotlinx.serialization.Serializable

@Serializable
sealed interface BottomSheetChildConfig {
  @Serializable data class Alarm(val mode: AlarmComponent.Mode) : BottomSheetChildConfig

  @Serializable data class Group(val mode: GroupComponent.Mode) : BottomSheetChildConfig
}
