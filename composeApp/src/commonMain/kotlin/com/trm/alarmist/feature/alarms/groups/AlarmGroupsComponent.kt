package com.trm.alarmist.feature.alarms.groups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.trm.alarmist.core.common.util.AnyStateFlow
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel

interface AlarmGroupsComponent {
  val groups: AnyStateFlow<List<AlarmGroupModel>>

  fun onEditAlarmClick(alarm: AlarmListModel)

  fun onToggleAlarmOnOff(alarm: AlarmListModel)

  fun onEditGroupClick(group: AlarmGroupModel)

  fun onToggleGroupOnOff(group: AlarmGroupModel)
}

class DefaultAlarmGroupsComponent(
  componentContext: ComponentContext,
  private val onEditAlarmClick: (AlarmListModel) -> Unit,
  private val onEditGroupClick: (AlarmGroupModel) -> Unit,
) : AlarmGroupsComponent, ComponentContext by componentContext {
  private val feature = instanceKeeper.getOrCreate(::AlarmGroupsFeature)

  override val groups: AnyStateFlow<List<AlarmGroupModel>> = feature.groups

  override fun onEditAlarmClick(alarm: AlarmListModel) {
    onEditAlarmClick.invoke(alarm)
  }

  override fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    feature.onToggleAlarmOnOff(alarm)
  }

  override fun onEditGroupClick(group: AlarmGroupModel) {
    onEditGroupClick.invoke(group)
  }

  override fun onToggleGroupOnOff(group: AlarmGroupModel) {
    feature.onToggleGroupOnOff(group)
  }
}
