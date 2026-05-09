package com.trm.alarmist.core.system.notification

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenter.Companion.currentNotificationCenter

class IosTimerNotifications(
  private val center: UNUserNotificationCenter = currentNotificationCenter()
) {
  fun scheduleElapsedNotification(timeIntervalSeconds: Double) {
    if (timeIntervalSeconds <= 0.0) return

    val content =
      UNMutableNotificationContent().apply {
        setTitle("Timer")
        setBody("Time is up.")
        setCategoryIdentifier(TIMER_CATEGORY_ID)
        setSound(UNNotificationSound.defaultSound())
      }

    val trigger =
      UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
        timeInterval = timeIntervalSeconds,
        repeats = false,
      )

    val request =
      UNNotificationRequest.requestWithIdentifier(
        identifier = TIMER_ELAPSED_REQUEST_ID,
        content = content,
        trigger = trigger,
      )

    center.addNotificationRequest(request, withCompletionHandler = null)
  }

  fun cancelElapsedNotification() {
    center.removePendingNotificationRequestsWithIdentifiers(listOf(TIMER_ELAPSED_REQUEST_ID))
    center.removeDeliveredNotificationsWithIdentifiers(listOf(TIMER_ELAPSED_REQUEST_ID))
  }

  companion object {
    const val TIMER_CATEGORY_ID = "TIMER_CATEGORY"
    const val TIMER_ELAPSED_REQUEST_ID = "TIMER_ELAPSED"
  }
}
