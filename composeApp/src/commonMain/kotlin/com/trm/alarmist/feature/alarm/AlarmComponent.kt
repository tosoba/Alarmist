package com.trm.alarmist.feature.alarm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer
import kotlinx.serialization.Serializable

interface AlarmComponent {
  val mode: Mode

  val feature: AlarmFeature

  fun onConfirmClick()

  @Serializable
  sealed interface Mode {
    @Serializable data object Add : Mode

    @Serializable data object Edit : Mode
  }
}

class DefaultAlarmComponent(
  componentContext: ComponentContext,
  override val mode: AlarmComponent.Mode,
  private val pop: () -> Unit,
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

  override fun onConfirmClick() {
    feature.onConfirmClick().invokeOnCompletion { pop() }
  }

  companion object {
    private const val SAVED_STATE_KEY = "ALARM_STATE"
  }
}
