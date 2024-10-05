package com.trm.alarmist.feature.timer

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleOwner

interface TimerComponent : LifecycleOwner

class DefaultTimerComponent(componentContext: ComponentContext) :
  TimerComponent, ComponentContext by componentContext
