package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.WidgetManager

class DeleteGroupUseCase(
  private val repository: AlarmRepository,
  private val widgetManager: WidgetManager,
) {
  suspend operator fun invoke(id: Long) {
    repository.deleteGroup(id)
    widgetManager.updateAllWidgets()
  }
}
