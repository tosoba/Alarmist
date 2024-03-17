package com.trm.alarmist.feature.alarms.upcoming

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.domain.model.AlarmListModel

interface UpcomingAlarmsComponent {
  val feature: UpcomingAlarmsFeature

  fun onAlarmClick(alarm: AlarmListModel)
}

class DefaultUpcomingAlarmsComponent(
  componentContext: ComponentContext,
  private val onEditAlarmClick: (AlarmListModel) -> Unit,
) : UpcomingAlarmsComponent, ComponentContext by componentContext {
  override val feature: UpcomingAlarmsFeature =
    instanceKeeper.getOrCreate {
      UpcomingAlarmsFeature(
        savedStateContainer =
          stateKeeper.consume(key = SAVED_STATE_KEY, strategy = SerializableContainer.serializer())
      )
    }

  override fun onAlarmClick(alarm: AlarmListModel) {
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
