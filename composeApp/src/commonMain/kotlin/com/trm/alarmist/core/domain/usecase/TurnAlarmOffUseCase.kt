package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.WidgetManager

class TurnAlarmOffUseCase(
  private val updateAlarmScheduleUseCase: UpdateAlarmScheduleUseCase,
  private val repository: AlarmRepository,
  private val widgetManager: WidgetManager,
) {
  suspend operator fun invoke(id: Long) {
    updateAlarmScheduleUseCase(repository.turnAlarmOff(id))
    widgetManager.updateAllWidgets()
  }
}
