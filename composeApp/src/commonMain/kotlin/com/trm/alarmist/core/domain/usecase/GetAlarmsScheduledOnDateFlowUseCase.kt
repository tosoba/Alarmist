package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toUpcomingListModel
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

class GetAlarmsScheduledOnDateFlowUseCase(private val repository: AlarmRepository) {
  operator fun invoke(date: LocalDate): Flow<List<UpcomingAlarmListModel>> {
    val now = LocalDateTime.now()
    return when {
      date < now.date -> {
        flowOf(emptyList())
      }
      date == now.date -> {
        combine(
          repository.getOneTimeAlarmsAfterTimeFlow(now.time).map { alarms ->
            alarms.map { it.toUpcomingListModel(scheduledAtDate = null, now = now) }
          },
          repository.getAlarmsScheduledToFireOnDateAfterTimeFlow(date, now.time).map { alarms ->
            alarms.map { it.toUpcomingListModel(scheduledAtDate = date, now = now) }
          },
          ::concatSortedByFireAtTime,
        )
      }
      date == now.date.plus(1, DateTimeUnit.DAY) -> {
        combine(
          repository.getOneTimeAlarmsBeforeTimeFlow(now.time).map { alarms ->
            alarms.map { it.toUpcomingListModel(scheduledAtDate = null, now = now) }
          },
          repository.getAlarmsScheduledToFireOnDateFlow(date).map { alarms ->
            alarms.map { it.toUpcomingListModel(scheduledAtDate = date, now = now) }
          },
          ::concatSortedByFireAtTime,
        )
      }
      else -> {
        repository.getAlarmsScheduledToFireOnDateFlow(date).map { alarms ->
          alarms.map { it.toUpcomingListModel(scheduledAtDate = date, now = now) }
        }
      }
    }
  }

  private fun concatSortedByFireAtTime(
    alarms1: List<UpcomingAlarmListModel>,
    alarms2: List<UpcomingAlarmListModel>,
  ): List<UpcomingAlarmListModel> = (alarms1 + alarms2).sortedBy(UpcomingAlarmListModel::fireAtTime)
}
