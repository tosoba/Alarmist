package com.trm.alarmist.core.system.alarm

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.missed_alarm
import alarmist.composeapp.generated.resources.missed_multiple_alarms
import alarmist.composeapp.generated.resources.missed_multiple_alarms_most_recent
import com.trm.alarmist.core.common.util.getStringBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenter.Companion.currentNotificationCenter

class IosAlarmNotifications(
  private val center: UNUserNotificationCenter = currentNotificationCenter()
) {
  fun scheduleFiredNotification(
    id: Long,
    name: String?,
    fireOnDateTime: LocalDateTime,
    soundId: String?,
  ) {
    val content =
      UNMutableNotificationContent().apply {
        setTitle(name ?: "Alarm")
        setBody(fireOnDateTime.time.toString())
        setCategoryIdentifier(ALARM_FIRED_CATEGORY_ID)
        setSound(UNNotificationSound.defaultSound())
        setUserInfo(
          mapOf("alarmId" to id.toString(), "fireOnDateTime" to fireOnDateTime.toString())
        )
      }

    val components = fireOnDateTime.toNSDateComponents()
    components.setNanosecond(0)
    val trigger =
      UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
        dateComponents = components,
        repeats = false,
      )

    val requestId = firedRequestId(id, fireOnDateTime)
    val request =
      UNNotificationRequest.requestWithIdentifier(
        identifier = requestId,
        content = content,
        trigger = trigger,
      )

    center.addNotificationRequest(request) { error ->
      if (error != null) {
        println("Error scheduling notification: $error")
      }
    }
  }

  fun scheduleUpcomingNotification(
    id: Long,
    name: String?,
    fireOnDateTime: LocalDateTime,
    upcomingDateTime: LocalDateTime,
  ) {
    val content =
      UNMutableNotificationContent().apply {
        setTitle("Upcoming Alarm")
        setBody("${name ?: "Alarm"} at ${fireOnDateTime.time}")
        setCategoryIdentifier(ALARM_UPCOMING_CATEGORY_ID)
        setSound(null)
        setUserInfo(
          mapOf("alarmId" to id.toString(), "fireOnDateTime" to fireOnDateTime.toString())
        )
      }

    val components = upcomingDateTime.toNSDateComponents()
    components.setNanosecond(0)
    val trigger =
      UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
        dateComponents = components,
        repeats = false,
      )

    val requestId = upcomingRequestId(id, fireOnDateTime)
    val request =
      UNNotificationRequest.requestWithIdentifier(
        identifier = requestId,
        content = content,
        trigger = trigger,
      )

    center.addNotificationRequest(request) { error ->
      if (error != null) {
        println("Error scheduling notification: $error")
      }
    }
  }

  fun cancelAlarmNotifications(id: Long) {
    center.getPendingNotificationRequestsWithCompletionHandler { requests ->
      val idsToRemove =
        requests
          ?.filterIsInstance<UNNotificationRequest>()
          ?.map { it.identifier }
          ?.filter { it.startsWith("ALARM_FIRED_${id}_") || it.startsWith("ALARM_UPCOMING_${id}_") }
          ?: return@getPendingNotificationRequestsWithCompletionHandler

      if (idsToRemove.isNotEmpty()) {
        center.removePendingNotificationRequestsWithIdentifiers(idsToRemove)
        center.removeDeliveredNotificationsWithIdentifiers(idsToRemove)
      }
    }
  }

  fun notifyAlarmMissed(id: Long, fireOnDateTime: LocalDateTime, name: String?) {
    val content =
      UNMutableNotificationContent().apply {
        setTitle(getStringBlocking(Res.string.missed_alarm))
        setBody(alarmNotificationContentText(fireOnDateTime, name))
        setUserInfo(mapOf("alarmId" to id.toString()))
      }

    val request =
      UNNotificationRequest.requestWithIdentifier(
        identifier = "ALARM_MISSED_$id",
        content = content,
        trigger = null,
      )

    center.addNotificationRequest(request) { error ->
      if (error != null) {
        println("Error scheduling notification: $error")
      }
    }
  }

  fun notifyMultipleAlarmsMissed(id: Long, fireOnDateTimes: List<LocalDateTime>, name: String?) {
    val content =
      UNMutableNotificationContent().apply {
        setTitle(getStringBlocking(Res.string.missed_multiple_alarms, fireOnDateTimes.size))
        setBody(
          "${getStringBlocking(Res.string.missed_multiple_alarms_most_recent)} ${
          alarmNotificationContentText(fireOnDateTimes.first(), name)
        }"
        )
        setUserInfo(mapOf("alarmId" to id.toString()))
      }

    val request =
      UNNotificationRequest.requestWithIdentifier(
        identifier = "ALARM_MISSED_$id",
        content = content,
        trigger = null,
      )

    center.addNotificationRequest(request) { error ->
      if (error != null) {
        println("Error scheduling notification: $error")
      }
    }
  }

  private fun alarmNotificationContentText(fireOnDateTime: LocalDateTime, name: String?): String =
    buildString {
      append(fireOnDateTime.time.toString())
      name?.let {
        append(" · ")
        append(it)
      }
    }

  companion object {
    const val ALARM_FIRED_CATEGORY_ID = "ALARM_FIRED_CATEGORY"
    const val ALARM_UPCOMING_CATEGORY_ID = "ALARM_UPCOMING_CATEGORY"

    fun firedRequestId(id: Long, fireOnDateTime: LocalDateTime): String {
      val epochMinutes = fireOnDateTime.toEpochMinutes()
      return "ALARM_FIRED_${id}_$epochMinutes"
    }

    fun upcomingRequestId(id: Long, fireOnDateTime: LocalDateTime): String {
      val epochMinutes = fireOnDateTime.toEpochMinutes()
      return "ALARM_UPCOMING_${id}_$epochMinutes"
    }

    private fun LocalDateTime.toEpochMinutes(): Long {
      return toInstant(TimeZone.currentSystemDefault()).epochSeconds / 60
    }
  }
}
