package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import kotlinx.datetime.LocalDateTime

class GetTodayAlarmsUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(): List<UpcomingAlarmListModel> {
    val now = LocalDateTime.now()
    return repository
      .getOneTimeAlarmsAfterTime(now.time)
      .plus(repository.getAlarmsScheduledToFireOnDateAfterTime(now.date, now.time))
      .sortedBy(UpcomingAlarmListModel::fireAtTime)
  }
}
