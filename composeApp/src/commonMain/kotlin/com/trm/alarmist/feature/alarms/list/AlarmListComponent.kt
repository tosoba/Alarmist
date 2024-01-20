package com.trm.alarmist.feature.alarms.list

import com.arkivanov.decompose.ComponentContext

interface AlarmListComponent

class DefaultAlarmListComponent(
    componentContext: ComponentContext,
) : AlarmListComponent, ComponentContext by componentContext
