package com.trm.alarmist.feature.alarm

import com.arkivanov.decompose.ComponentContext

interface AlarmComponent {
  val mode: Mode

  sealed interface Mode {
    data object Add : Mode

    data object Edit : Mode
  }
}

class DefaultAlarmComponent(
  componentContext: ComponentContext,
  override val mode: AlarmComponent.Mode,
) : AlarmComponent, ComponentContext by componentContext
