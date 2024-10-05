package com.trm.alarmist.feature.stopwatch

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleOwner

interface StopwatchComponent : LifecycleOwner

class DefaultStopwatchComponent(componentContext: ComponentContext) :
  StopwatchComponent, ComponentContext by componentContext
