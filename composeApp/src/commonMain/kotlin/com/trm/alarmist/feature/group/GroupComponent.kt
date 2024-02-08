package com.trm.alarmist.feature.group

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable

interface GroupComponent {
  val mode: Mode

  @Serializable
  sealed interface Mode {
    @Serializable data object Add : Mode

    @Serializable data object Edit : Mode
  }
}

class DefaultGroupComponent(
  componentContext: ComponentContext,
  override val mode: GroupComponent.Mode,
) : GroupComponent, ComponentContext by componentContext
