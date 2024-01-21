package com.trm.alarmist.feature.timer

import com.arkivanov.decompose.ComponentContext

interface TimerComponent {}

class DefaultTimerComponent(
  componentContext: ComponentContext,
) : TimerComponent, ComponentContext by componentContext
