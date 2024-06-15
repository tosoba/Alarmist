package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.WidgetManager

class DeleteAlarmUseCase(
  private val repository: AlarmRepository,
  private val scheduler: AlarmScheduler,
  private val widgetManager: WidgetManager,
) {
  suspend operator fun invoke(id: Long) {
    repository.deleteAlarm(id)
    scheduler.cancelAlarm(id)
    widgetManager.updateAllWidgets()
  }
}
