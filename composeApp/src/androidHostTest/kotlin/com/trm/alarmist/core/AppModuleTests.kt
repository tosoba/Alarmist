package com.trm.alarmist.core

import com.trm.alarmist.appModule
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.WidgetManager
import com.trm.alarmist.feature.stopwatch.StopwatchScreenProvider
import com.trm.alarmist.feature.timer.TimerScreenProvider
import com.trm.alarmist.feature.widgets.WidgetScreenProvider
import kotlin.test.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class AppModuleTests {
  @Test
  fun `appModule should be verified positively`() {
    appModule.verify(
      extraTypes =
        listOf(
          WidgetManager::class,
          AlarmScheduler::class,
          WidgetScreenProvider::class,
          StopwatchScreenProvider::class,
          TimerScreenProvider::class,
        )
    )
  }
}
