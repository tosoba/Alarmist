package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.WidgetManager
import kotlinx.datetime.LocalDateTime

class UpdateAlarmOnDismissUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
  private val widgetManager: WidgetManager,
) {
  suspend operator fun invoke(id: Long, notificationDateTime: LocalDateTime) {
    updateAlarmScheduleUseCase(
      alarm = repository.updateAlarmOnDismiss(id, notificationDateTime),
      afterDateTime = notificationDateTime,
    )

    widgetManager.updateAllWidgets()
  }
}
