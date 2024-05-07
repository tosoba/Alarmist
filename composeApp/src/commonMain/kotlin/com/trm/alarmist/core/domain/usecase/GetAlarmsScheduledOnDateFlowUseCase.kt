package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

class GetAlarmsScheduledOnDateFlowUseCase(private val repository: AlarmRepository) {
  operator fun invoke(date: LocalDate): Flow<List<AlarmListModel>> {
    val now = LocalDateTime.now()
    return when {
      date < now.date -> {
        flowOf(emptyList())
      }
      date == now.date -> {
        combine(
          repository.getOnOneTimeAlarmsAfterTimeFlow(now.time),
          repository.getOnAlarmsScheduledToFireOnDateAfterTimeFlow(date, now.time),
          ::concatSortedByFireAtTime,
        )
      }
      date == now.date.plus(1, DateTimeUnit.DAY) -> {
        combine(
          repository.getOnOneTimeAlarmsBeforeTimeFlow(now.time),
          repository.getOnAlarmsScheduledToFireOnDateFlow(date),
          ::concatSortedByFireAtTime,
        )
      }
      else -> {
        repository.getOnAlarmsScheduledToFireOnDateFlow(date)
      }
    }
  }

  private fun concatSortedByFireAtTime(
    alarms1: List<AlarmListModel>,
    alarms2: List<AlarmListModel>,
  ): List<AlarmListModel> = (alarms1 + alarms2).sortedBy(AlarmListModel::nextFireAtTime)
}
