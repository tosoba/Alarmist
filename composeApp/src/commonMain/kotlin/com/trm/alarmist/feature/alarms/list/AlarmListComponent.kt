package com.trm.alarmist.feature.alarms.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.domain.model.AlarmListItem

interface AlarmListComponent {
  val alarms: AnyStateFlow<List<AlarmListItem>>

  fun onAlarmClick(alarm: AlarmListItem)

  fun onToggleAlarmOnOff(alarm: AlarmListItem)
}

class DefaultAlarmListComponent(componentContext: ComponentContext) :
  AlarmListComponent, ComponentContext by componentContext {
  private val feature = instanceKeeper.getOrCreate(::AlarmListFeature)

  override val alarms: AnyStateFlow<List<AlarmListItem>> = feature.alarms

  override fun onAlarmClick(alarm: AlarmListItem) {
    // TODO:
  }

  override fun onToggleAlarmOnOff(alarm: AlarmListItem) {
    feature.onToggleAlarmOnOff(alarm)
  }
}
