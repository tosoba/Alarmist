package com.trm.alarmist.core.system.alarm

import com.trm.alarmist.core.domain.usecase.GetAndResetMissedAlarmsOnBootUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.UserNotifications.UNNotificationDefaultActionIdentifier

object IosAlarmNotificationActionBridge : KoinComponent {
  private val updateAlarmOnDismissUseCase: UpdateAlarmOnDismissUseCase by inject()
  private val getAndResetMissedAlarmsOnBootUseCase: GetAndResetMissedAlarmsOnBootUseCase by inject()
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  fun handle(actionId: String, userInfo: Map<Any?, *>) {
    val alarmId = (userInfo["alarmId"] as? String)?.toLongOrNull() ?: return
    val fireOnDateTimeStr = (userInfo["fireOnDateTime"] as? String) ?: return
    val fireOnDateTime =
      try {
        LocalDateTime.parse(fireOnDateTimeStr)
      } catch (e: Exception) {
        return
      }

    scope.launch {
      when (actionId) {
        ACTION_DISMISS -> {
          updateAlarmOnDismissUseCase(alarmId, fireOnDateTime)
          IosAlarmEnvironment.notifications.cancelAlarmNotifications(alarmId)
        }
        UNNotificationDefaultActionIdentifier -> {}
        else -> Unit
      }
    }
  }

  fun checkMissedAlarms() {
    scope.launch {
      val missedAlarms = getAndResetMissedAlarmsOnBootUseCase()
      missedAlarms.forEach { (alarm, missedDateTimes) ->
        if (missedDateTimes.size == 1) {
          IosAlarmEnvironment.notifications.notifyAlarmMissed(
            id = alarm.id,
            fireOnDateTime = missedDateTimes.first(),
            name = alarm.name,
          )
        } else if (missedDateTimes.size > 1) {
          IosAlarmEnvironment.notifications.notifyMultipleAlarmsMissed(
            id = alarm.id,
            fireOnDateTimes = missedDateTimes,
            name = alarm.name,
          )
        }
      }
    }
  }

  const val ACTION_DISMISS = "ALARM_DISMISS"
}
