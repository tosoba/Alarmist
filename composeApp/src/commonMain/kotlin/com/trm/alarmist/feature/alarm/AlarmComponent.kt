package com.trm.alarmist.feature.alarm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.serialization.Serializable

interface AlarmComponent {
  val mode: Mode

  val feature: AlarmFeature

  @Serializable
  sealed interface Mode {
    @Serializable data object Add : Mode

    @Serializable data class Edit(val alarm: AlarmListModel) : Mode
  }
}

class DefaultAlarmComponent(
  componentContext: ComponentContext,
  override val mode: AlarmComponent.Mode,
) : AlarmComponent, ComponentContext by componentContext {
  override val feature =
    instanceKeeper.getOrCreate {
      AlarmFeature(
        savedStateContainer =
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
