package com.trm.alarmist.core.system.timer

object IosTimerNotificationActionBridge {
  fun handle(actionId: String) {
    val controller = IosTimerEnvironment.controller

    when (actionId) {
      ACTION_CANCEL -> controller.cancel()
      else -> Unit
    }
  }

  const val ACTION_CANCEL = "TIMER_CANCEL"
}
