package com.trm.alarmist.feature.alarms

import com.arkivanov.decompose.ComponentContext

interface AlarmsComponent {}

class DefaultAlarmsComponent(
    componentContext: ComponentContext,
) : AlarmsComponent, ComponentContext by componentContext
