package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.datetime.LocalDateTime

class UpdateAlarmOnNotificationUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
) {
  suspend operator fun invoke(id: Long, notificationDateTime: LocalDateTime) {
    updateAlarmScheduleUseCase(repository.updateAlarmOnNotification(id, notificationDateTime))
  }
}
