package com.trm.alarmist.feature.widget.config.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer

interface GroupWidgetConfigComponent {
  val feature: GroupWidgetConfigFeature
}

class DefaultGroupWidgetConfigComponent(componentContext: ComponentContext) :
  GroupWidgetConfigComponent, ComponentContext by componentContext {
  override val feature: GroupWidgetConfigFeature =
    instanceKeeper.getOrCreate {
      GroupWidgetConfigFeature(
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
    private const val SAVED_STATE_KEY = "GROUP_WIDGET_CONFIG_STATE"
  }
}
