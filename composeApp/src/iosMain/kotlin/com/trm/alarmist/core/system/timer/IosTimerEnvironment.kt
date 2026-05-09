package com.trm.alarmist.core.system.timer

import com.trm.alarmist.core.system.notification.IosTimerNotifications

object IosTimerEnvironment {
  val notifications: IosTimerNotifications by lazy { IosTimerNotifications() }
  val controller: IosTimerController by lazy { IosTimerController(notifications = notifications) }
}
