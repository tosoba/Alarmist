package com.trm.alarmist.feature.widgets

import com.arkivanov.decompose.ComponentContext

interface WidgetsComponent

class DefaultWidgetsComponent(componentContext: ComponentContext) :
  WidgetsComponent, ComponentContext by componentContext
