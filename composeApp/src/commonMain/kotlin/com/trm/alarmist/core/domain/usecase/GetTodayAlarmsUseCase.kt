package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListStatus
import kotlinx.datetime.LocalDateTime

class GetTodayAlarmsUseCase(private val repository: AlarmRepository) {
  suspend operator fun invoke(): List<UpcomingAlarmListModel> {
    val now = LocalDateTime.now()
    return repository
      .getOneTimeAlarmsAfterTime(now.time)
      .plus(repository.getAlarmsScheduledToFireOnDateAfterTime(now.date, now.time))
      .sortedWith(
        compareBy(
          { alarm ->
            when (alarm.status) {
              UpcomingAlarmListStatus.ON -> 0
              UpcomingAlarmListStatus.OFF_ON_DATE,
              UpcomingAlarmListStatus.OFF -> 1
            }
          },
          UpcomingAlarmListModel::fireAtTime,
        )
      )
  }
}
