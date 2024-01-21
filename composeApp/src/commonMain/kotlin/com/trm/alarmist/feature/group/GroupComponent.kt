package com.trm.alarmist.feature.group

import com.arkivanov.decompose.ComponentContext

interface GroupComponent {
  val mode: Mode

  sealed interface Mode {
    data object Add : Mode

    data object Edit : Mode
  }
}

class DefaultGroupComponent(
  componentContext: ComponentContext,
  override val mode: GroupComponent.Mode,
) : GroupComponent, ComponentContext by componentContext
