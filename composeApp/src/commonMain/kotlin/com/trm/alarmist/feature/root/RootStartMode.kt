package com.trm.alarmist.feature.root

sealed interface RootStartMode {
  data object Normal : RootStartMode

  data class EditAlarm(val id: Long) : RootStartMode

  data object AddAlarm : RootStartMode

  data object Stopwatch : RootStartMode
}
