package com.trm.alarmist.feature.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import kotlinx.serialization.Serializable

interface GroupComponent {
  val mode: Mode

  val feature: GroupFeature

  @Serializable
  sealed interface Mode {
    @Serializable data object Add : Mode

    @Serializable data class Edit(val group: AlarmGroupModel) : Mode
  }
}

class DefaultGroupComponent(
  componentContext: ComponentContext,
  override val mode: GroupComponent.Mode,
) : GroupComponent, ComponentContext by componentContext {
  override val feature = instanceKeeper.getOrCreate {
    GroupFeature(
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
    private const val SAVED_STATE_KEY = "GROUP_STATE"
  }
}
