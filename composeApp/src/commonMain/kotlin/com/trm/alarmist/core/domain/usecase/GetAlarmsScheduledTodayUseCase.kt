package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.datetime.LocalDateTime

class GetAlarmsScheduledTodayUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(): List<AlarmListModel> {
    val now = LocalDateTime.now()
    return (repository.getOnOneTimeAlarmsAfterTime(now.time) +
        repository.getOnAlarmsScheduledToFireOnDateAfterTime(now.date, now.time))
      .sortedBy(AlarmListModel::nextFireAtTime)
  }
}
