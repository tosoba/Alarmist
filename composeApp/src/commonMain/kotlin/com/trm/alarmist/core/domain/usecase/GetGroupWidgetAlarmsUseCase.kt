package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toListModel
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.WidgetAlarmListModel
import kotlinx.datetime.LocalDateTime

class GetGroupWidgetAlarmsUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(groupId: Long): List<WidgetAlarmListModel> =
    repository.getAlarmsInGroup(groupId).map { alarm ->
      WidgetAlarmListModel(alarm.toListModel(LocalDateTime.now()))
    }
}
