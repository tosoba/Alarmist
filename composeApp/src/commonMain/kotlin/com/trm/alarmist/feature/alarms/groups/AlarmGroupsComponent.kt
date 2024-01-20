package com.trm.alarmist.feature.alarms.groups

import com.arkivanov.decompose.ComponentContext

interface AlarmGroupsComponent

class DefaultAlarmGroupsComponent(
    componentContext: ComponentContext,
) : AlarmGroupsComponent, ComponentContext by componentContext
