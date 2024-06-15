package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.WidgetManager

class EditGroupUseCase(
  private val repository: AlarmRepository,
  private val widgetManager: WidgetManager,
) {
  suspend operator fun invoke(id: Long, name: String, color: Int, alarmIds: Collection<Long>) {
    repository.editGroup(id, name, color, alarmIds)
    widgetManager.updateAllWidgets()
  }
}
