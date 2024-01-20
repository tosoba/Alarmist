package com.trm.alarmist.feature.alarm

import com.arkivanov.decompose.ComponentContext

interface AlarmComponent {
  sealed interface Mode {
    data object Add : Mode

    data object Edit : Mode
  }
}

class DefaultAlarmComponent(
    private val mode: AlarmComponent.Mode,
    componentContext: ComponentContext,
) : AlarmComponent, ComponentContext by componentContext
