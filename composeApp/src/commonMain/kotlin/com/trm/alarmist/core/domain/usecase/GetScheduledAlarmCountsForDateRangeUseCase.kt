package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

class GetScheduledAlarmCountsForDateRangeUseCase(private val repository: AlarmRepository) {
  operator fun invoke(range: ClosedRange<LocalDate>): Flow<Map<LocalDate, Int>> {
    val now = LocalDateTime.now()
    val tommorow = now.date.plus(1, DateTimeUnit.DAY)
    val coalescedRange = if (range.start < now.date) now.date..range.endInclusive else range
    return combine(
      if (now.date in coalescedRange) {
        repository.countOnOneTimeAlarmsAfterTime(now.time)
      } else {
        flowOf(0)
      },
      if (tommorow in coalescedRange) {
        repository.countOnOneTimeAlarmsBeforeTime(now.time)
      } else {
        flowOf(0)
      },
      repository.getOnAlarmSchedulesForDates(coalescedRange).map { schedules ->
        List(coalescedRange.endInclusive.toEpochDays() - coalescedRange.start.toEpochDays() + 1) {
            coalescedRange.start.plus(it, DateTimeUnit.DAY)
          }
          .associateWith { date ->
            schedules.count {
              date in it.scheduledOnDates || date.dayOfWeek in it.scheduledOnDaysOfWeek
            }
          }
          .toMutableMap()
      },
    ) { oneTimeTodayCount, oneTimeTommorowCount, scheduledCounts ->
      scheduledCounts[now.date]?.let { scheduledCounts[now.date] = it + oneTimeTodayCount }
      scheduledCounts[tommorow]?.let { scheduledCounts[tommorow] = it + oneTimeTommorowCount }
      scheduledCounts
    }
  }
}
