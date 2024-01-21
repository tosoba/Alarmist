package com.trm.alarmist.feature.clock

import com.arkivanov.decompose.ComponentContext

interface ClockComponent {}

class DefaultClockComponent(
  componentContext: ComponentContext,
) : ClockComponent, ComponentContext by componentContext
