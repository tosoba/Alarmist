package com.trm.alarmist.feature.alarm

import com.arkivanov.decompose.ComponentContext

interface AlarmComponent {
  sealed interface Mode {
    data object Add : Mode

    data object Edit : Mode
  }
}

class DefaultAlarmComponent(
  componentContext: ComponentContext,
  private val mode: AlarmComponent.Mode,
) : AlarmComponent, ComponentContext by componentContext
