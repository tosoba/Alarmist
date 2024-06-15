package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.WidgetManager

class AddGroupUseCase(
  private val repository: AlarmRepository,
  private val widgetManager: WidgetManager,
) {
  suspend operator fun invoke(name: String, color: Int, alarmIds: Collection<Long>) {
    repository.addGroup(name, color, alarmIds)
    widgetManager.updateAllWidgets()
  }
}
