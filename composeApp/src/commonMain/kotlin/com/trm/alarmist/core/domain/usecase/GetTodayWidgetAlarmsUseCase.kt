package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toUpcomingListModel
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.WidgetAlarmListModel
import kotlinx.datetime.LocalDateTime

class GetTodayWidgetAlarmsUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(): List<WidgetAlarmListModel> {
    val now = LocalDateTime.now()
    return repository
      .getPartitionedAlarmsAfterDateTime(now)
      .run {
        oneTime.map { it.toUpcomingListModel(scheduledAtDate = null, now = now) } +
          scheduled.map { it.toUpcomingListModel(scheduledAtDate = now.date, now = now) }
      }
      .sortedBy(UpcomingAlarmListModel::fireAtTime)
      .map(::WidgetAlarmListModel)
  }
}
