package com.trm.alarmist.core.system.permission

import androidx.compose.runtime.Composable
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenter.Companion.currentNotificationCenter
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun postNotificationsPermissionHandler(onGranted: () -> Unit): () -> Unit {
  val center: UNUserNotificationCenter = currentNotificationCenter()
  return {
    center.requestAuthorizationWithOptions(
      options =
        UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound
    ) { granted, _ ->
      if (granted) {
        dispatch_async(dispatch_get_main_queue()) { onGranted() }
      }
    }
  }
}
