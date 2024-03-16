package com.trm.alarmist.feature.alarms.upcoming

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer

interface UpcomingAlarmsComponent {
  val feature: UpcomingAlarmsFeature
}

class DefaultUpcomingAlarmsComponent(componentContext: ComponentContext) :
  UpcomingAlarmsComponent, ComponentContext by componentContext {
  override val feature: UpcomingAlarmsFeature =
    instanceKeeper.getOrCreate {
      UpcomingAlarmsFeature(
        savedStateContainer =
          stateKeeper.consume(key = SAVED_STATE_KEY, strategy = SerializableContainer.serializer())
      )
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
