package com.trm.alarmist.feature.root

import com.trm.alarmist.core.common.model.CommonParcelable
import com.trm.alarmist.core.common.model.CommonParcelize

sealed interface RootStartMode : CommonParcelable {
  @CommonParcelize data object Normal : RootStartMode

  @CommonParcelize data class EditAlarm(val id: Long) : RootStartMode

  @CommonParcelize data object AddAlarm : RootStartMode

  @CommonParcelize data object Stopwatch : RootStartMode

  companion object {
    const val EXTRA_KEY = "RootStartMode"
  }
}
