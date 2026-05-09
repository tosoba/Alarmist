package com.trm.alarmist.core.system.timer

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object IosTimerNotificationActionBridge : KoinComponent {
  private val controller: IosTimerController by inject()

  fun handle(actionId: String) {
    when (actionId) {
      ACTION_CANCEL -> controller.cancel()
      else -> Unit
    }
  }

  const val ACTION_CANCEL = "TIMER_CANCEL"
}
