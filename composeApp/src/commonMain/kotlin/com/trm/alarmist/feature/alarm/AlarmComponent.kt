package com.trm.alarmist.feature.alarm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer

interface AlarmComponent {
  val mode: Mode

  val feature: AlarmFeature

  sealed interface Mode {
    data object Add : Mode

    data object Edit : Mode
  }
}

class DefaultAlarmComponent(
  componentContext: ComponentContext,
  override val mode: AlarmComponent.Mode,
) : AlarmComponent, ComponentContext by componentContext {
  override val feature =
    instanceKeeper.getOrCreate {
      AlarmFeature(
        savedState =
          stateKeeper.consume(key = SAVED_STATE_KEY, strategy = SerializableContainer.serializer()),
        mode = mode,
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
    private const val SAVED_STATE_KEY = "ALARM_STATE"
  }
}
