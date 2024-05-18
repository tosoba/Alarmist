package com.trm.alarmist.feature.alarms.upcoming

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel

interface UpcomingAlarmsComponent {
  val feature: UpcomingAlarmsFeature

  fun onAlarmClick(alarm: UpcomingAlarmListModel)
}

class DefaultUpcomingAlarmsComponent(
  componentContext: ComponentContext,
  private val onEditAlarmClick: (UpcomingAlarmListModel) -> Unit,
) : UpcomingAlarmsComponent, ComponentContext by componentContext {
  override val feature: UpcomingAlarmsFeature =
    instanceKeeper.getOrCreate {
      UpcomingAlarmsFeature(
        savedStateContainer =
          stateKeeper.consume(key = SAVED_STATE_KEY, strategy = SerializableContainer.serializer())
      )
    }

  override fun onAlarmClick(alarm: UpcomingAlarmListModel) {
    onEditAlarmClick(alarm)
  }

  init {
    stateKeeper.register(
      key = SAVED_STATE_KEY,
      strategy = SerializableContainer.serializer(),
      supplier = feature::saveState,
    )
  }

  companion object {
    private const val SAVED_STATE_KEY = "UPCOMING_ALARMS_STATE"
  }
}
