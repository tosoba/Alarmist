package com.trm.alarmist.feature.alarm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.datetime.LocalTime

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
) : AlarmComponent, ComponentContext by componentContext {
  private val feature = instanceKeeper.getOrCreate(::AlarmFeature)

  // TODO: keep fireAt time as state here with proper saving into bundle

  fun addAlarm(fireAt: LocalTime) {
    feature.addAlarm(fireAt, null) // TODO: name
  }
}
