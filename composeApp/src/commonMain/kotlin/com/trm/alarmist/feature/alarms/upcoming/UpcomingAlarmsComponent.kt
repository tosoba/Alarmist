package com.trm.alarmist.feature.alarms.upcoming

import com.arkivanov.decompose.ComponentContext

interface UpcomingAlarmsComponent

class DefaultUpcomingAlarmsComponent(
  componentContext: ComponentContext,
) : UpcomingAlarmsComponent, ComponentContext by componentContext
