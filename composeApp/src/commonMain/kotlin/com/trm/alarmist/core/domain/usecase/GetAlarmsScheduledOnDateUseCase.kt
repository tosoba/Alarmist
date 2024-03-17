package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

class GetAlarmsScheduledOnDateUseCase(private val repository: AlarmRepository) {
  operator fun invoke(date: LocalDate): Flow<List<AlarmListModel>> {
    val now = LocalDateTime.now()
    return when {
      date < now.date -> flowOf(emptyList())
      date == now.date -> {
        combine(
          repository.getOnOneTimeAlarms().map { alarms ->
            alarms.filter { it.fireAtTime > now.time }
          },
          repository.getOnAlarmsScheduledToFireOnDate(date),
          ::combineSortedByFireAtTime,
        )
      }
      date == now.date.plus(1, DateTimeUnit.DAY) -> {
        combine(
          repository.getOnOneTimeAlarms().map { alarms ->
            alarms.filter { it.fireAtTime < now.time }
          },
          repository.getOnAlarmsScheduledToFireOnDate(date),
          ::combineSortedByFireAtTime,
        )
      }
      else -> {
        repository.getOnAlarmsScheduledToFireOnDate(date)
      }
    }
  }

  private fun combineSortedByFireAtTime(
    alarms1: List<AlarmListModel>,
    alarms2: List<AlarmListModel>,
  ): List<AlarmListModel> = (alarms1 + alarms2).sortedBy(AlarmListModel::fireAtTime)
}
