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

  fun onEditAlarmClick(alarm: AlarmListModel)

  fun onEditGroupClick(group: AlarmGroupModel)
}

class DefaultAlarmGroupsComponent(
  componentContext: ComponentContext,
  private val onEditAlarmClick: (AlarmListModel) -> Unit,
  private val onEditGroupClick: (AlarmGroupModel) -> Unit,
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

  override fun onEditAlarmClick(alarm: AlarmListModel) {
    onEditAlarmClick.invoke(alarm)
  }

  override fun onEditGroupClick(group: AlarmGroupModel) {
    onEditGroupClick.invoke(group)
  }

  companion object {
    private const val SAVED_STATE_KEY = "ALARM_GROUPS_STATE"
  }
}
