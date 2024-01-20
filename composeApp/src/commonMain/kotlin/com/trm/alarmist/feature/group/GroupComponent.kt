package com.trm.alarmist.feature.group

import com.arkivanov.decompose.ComponentContext

interface GroupComponent {
  sealed interface Mode {
    data object Add : Mode

    data object Edit : Mode
  }
}

class DefaultGroupComponent(
    val mode: GroupComponent.Mode,
    componentContext: ComponentContext,
) : GroupComponent, ComponentContext by componentContext
