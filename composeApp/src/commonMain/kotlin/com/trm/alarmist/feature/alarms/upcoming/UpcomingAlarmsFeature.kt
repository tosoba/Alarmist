package com.trm.alarmist.feature.alarms.upcoming

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.now
import kotlinx.datetime.LocalDate

class UpcomingAlarmsFeature(savedStateContainer: SerializableContainer?) : CoroutineFeature() {
  var state: UpcomingAlarmsState =
    savedStateContainer?.consume(strategy = UpcomingAlarmsState.serializer())
      ?: with(LocalDate.now()) { UpcomingAlarmsState(null, month, year) }
    private set

  fun saveState(): SerializableContainer =
    SerializableContainer(value = state, strategy = UpcomingAlarmsState.serializer())
}
