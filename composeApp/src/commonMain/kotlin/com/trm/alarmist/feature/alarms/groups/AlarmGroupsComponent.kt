package com.trm.alarmist.feature.alarms.groups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.coroutines.flow.StateFlow

interface AlarmGroupsComponent {
  val state: StateFlow<AlarmGroupsState>

  val feature: AlarmGroupsFeature

  val onEditAlarmClick: (AlarmListModel) -> Unit

  val onEditGroupClick: (AlarmGroupModel) -> Unit
}

class DefaultAlarmGroupsComponent(
  componentContext: ComponentContext,
  override val onEditAlarmClick: (AlarmListModel) -> Unit,
  override val onEditGroupClick: (AlarmGroupModel) -> Unit,
) : AlarmGroupsComponent, ComponentContext by componentContext {
  override val feature: AlarmGroupsFeature =
    instanceKeeper.getOrCreate {
      AlarmGroupsFeature(
        stateKeeper.consume(key = SAVED_STATE_KEY, strategy = SerializableContainer.serializer())
      )
    }

  override val state: StateFlow<AlarmGroupsState> = feature.state

  init {
    stateKeeper.register(
      key = SAVED_STATE_KEY,
      strategy = SerializableContainer.serializer(),
      supplier = feature::saveState,
    )
  }

  companion object {
    private const val SAVED_STATE_KEY = "ALARM_GROUPS_STATE"
  }
}
