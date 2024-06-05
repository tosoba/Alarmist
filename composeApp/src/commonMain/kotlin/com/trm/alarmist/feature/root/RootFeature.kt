package com.trm.alarmist.feature.root

import com.trm.alarmist.core.common.CoroutineFeature
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootFeature : CoroutineFeature(), KoinComponent {
  private val repository: AlarmRepository by inject()

  init {
    flow {
        emit(Unit)

        var prevTime = LocalTime.now()
        while (true) {
          delay(1_000L)
          val currentTime = LocalTime.now()
          if (currentTime.minute != prevTime.minute) {
            prevTime = currentTime
            emit(Unit)
          }
        }
      }
      .onEach { repository.resetPastOffAlarmsScheduledOnDatesOnly() }
      .launchIn(coroutineScope)
  }
}
