package com.trm.alarmist.feature.alarms.list

import kotlinx.datetime.LocalTime

data class AlarmListItem(val fireAt: LocalTime, val name: String?)
