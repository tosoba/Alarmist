package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.shouldFireOn
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus

class GetAndUpdateOnAlarmsWithMissedTimestampsOnBootUseCase(
  private val repository: AlarmRepository
) {
  suspend operator fun invoke(): Map<AlarmModel, List<LocalDateTime>> {
    val now = LocalDateTime.now()
    return repository
      .getAndUpdateOnAlarms()
      .associateWith {
        val missedDateTimes = mutableListOf<LocalDateTime>()

        val limit =
          maxOf(
            it.lastNotificationDate?.atTime(it.fireAtTime) ?: it.lastModificationDateTime,
            it.lastModificationDateTime,
          )
        var current =
          if (now.time < it.fireAtTime) {
            now.date.minus(1, DateTimeUnit.DAY).atTime(it.fireAtTime)
          } else {
            LocalDateTime(now.date, it.fireAtTime)
          }
        while (current > limit) {
          if (it.shouldFireOn(current.date)) {
            missedDateTimes.add(current)
          }
          current = current.date.minus(1, DateTimeUnit.DAY).atTime(it.fireAtTime)
        }

        missedDateTimes
      }
      .filterValues(List<LocalDateTime>::isNotEmpty)
  }
}
