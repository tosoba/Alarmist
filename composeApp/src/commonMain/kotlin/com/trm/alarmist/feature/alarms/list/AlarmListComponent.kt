package com.trm.alarmist.feature.alarms.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.trm.alarmist.core.common.model.Initializable
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.coroutines.flow.StateFlow

interface AlarmListComponent {
  val alarms: StateFlow<Initializable<List<AlarmListModel>>>
  val groups: StateFlow<Map<Long, AlarmGroupModel>>

  fun onAlarmClick(alarm: AlarmListModel)

  fun onToggleAlarmOnOff(alarm: AlarmListModel)
}

class DefaultAlarmListComponent(
  componentContext: ComponentContext,
  private val onEditAlarmClick: (AlarmListModel) -> Unit,
) : AlarmListComponent, ComponentContext by componentContext {
  private val feature = instanceKeeper.getOrCreate(::AlarmListFeature)

  override val alarms: StateFlow<Initializable<List<AlarmListModel>>> = feature.alarms
  override val groups: StateFlow<Map<Long, AlarmGroupModel>> = feature.groups

  override fun onAlarmClick(alarm: AlarmListModel) {
    onEditAlarmClick(alarm)
  }

  override fun onToggleAlarmOnOff(alarm: AlarmListModel) {
    feature.onToggleAlarmOnOff(alarm)
  }
}
