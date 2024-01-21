package com.trm.alarmist.feature.group

import com.arkivanov.decompose.ComponentContext

interface GroupComponent {
  sealed interface Mode {
    data object Add : Mode

    data object Edit : Mode
  }
}

class DefaultGroupComponent(
  componentContext: ComponentContext,
  val mode: GroupComponent.Mode,
) : GroupComponent, ComponentContext by componentContext
