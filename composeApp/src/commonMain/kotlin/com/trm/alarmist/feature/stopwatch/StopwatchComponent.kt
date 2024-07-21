package com.trm.alarmist.feature.stopwatch

import com.arkivanov.decompose.ComponentContext

interface StopwatchComponent

class DefaultStopwatchComponent(componentContext: ComponentContext) :
  StopwatchComponent, ComponentContext by componentContext
