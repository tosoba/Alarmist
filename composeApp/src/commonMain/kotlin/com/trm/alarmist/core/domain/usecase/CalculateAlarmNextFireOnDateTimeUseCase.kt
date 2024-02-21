package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

class CalculateAlarmNextFireOnDateTimeUseCase {
  operator fun invoke(alarm: AlarmModel): LocalDateTime? =
    invoke(
      fireAtTime = alarm.fireAtTime,
      scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek,
      scheduledOnDates = alarm.scheduledOnDates,
      offOnDates = alarm.offOnDates,
    )

  operator fun invoke(
    fireAtTime: LocalTime,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ): LocalDateTime? {
    val now = LocalDateTime.now()

    if (scheduledOnDaysOfWeek.isEmpty() && scheduledOnDates.isEmpty()) {
      return when {
        fireAtTime < now.time -> LocalDate.now().plus(1, DateTimeUnit.DAY)
        else -> LocalDate.now()
      }.atTime(fireAtTime)
    }

    fun DayOfWeek.nextScheduledOnDate(): LocalDate {
      var currentDate = now.date
      while (currentDate.dayOfWeek != this) {
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
      }
      while (currentDate.atTime(fireAtTime) < now || currentDate in offOnDates) {
        currentDate = currentDate.plus(1, DateTimeUnit.WEEK)
      }
      return currentDate
    }

    val nextScheduledOnDayOfWeek = scheduledOnDaysOfWeek.minOfOrNull(DayOfWeek::nextScheduledOnDate)
    val nextScheduledOnDate =
      scheduledOnDates
        .run { if (offOnDates.isEmpty()) this else filter { it !in offOnDates } }
        .minOrNull()
    return when {
      nextScheduledOnDayOfWeek != null && nextScheduledOnDate != null -> {
        minOf(nextScheduledOnDayOfWeek, nextScheduledOnDate)
      }
      nextScheduledOnDayOfWeek != null -> {
        nextScheduledOnDayOfWeek
      }
      nextScheduledOnDate != null -> {
        nextScheduledOnDate
      }
      else -> {
        null
      }
    }?.atTime(fireAtTime)
  }
}
