package com.trm.alarmist.feature.alarms.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel

interface AlarmListComponent {
  val alarms: AnyStateFlow<List<AlarmListModel>>
  val groups: AnyStateFlow<Map<Long, AlarmGroupModel>>

  fun onAlarmClick(alarm: AlarmListModel)

  fun onToggleAlarmOnOff(alarm: AlarmListModel)
}

class DefaultAlarmListComponent(
  componentContext: ComponentContext,
  private val onEditAlarmClick: (AlarmListModel) -> Unit,
) : AlarmListComponent, ComponentContext by componentContext {
  private val feature = instanceKeeper.getOrCreate(::AlarmListFeature)

  override val alarms: AnyStateFlow<List<AlarmListModel>> = feature.alarms
  override val groups: AnyStateFlow<Map<Long, AlarmGroupModel>> = feature.groups

  override fun onAlarmClick(alarm: AlarmListModel) {
    onEditAlarmClick(alarm)
  }

  override fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    feature.onToggleAlarmOnOff(alarm)
  }
}
