package com.trm.alarmist.core.data

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListItem
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.db.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus

class AlarmLocalRepository(
  private val db: AlarmistDatabase,
  private val scheduler: AlarmScheduler,
) : AlarmRepository {
  override suspend fun addAlarm(
    fireAt: LocalTime,
    name: String?,
    isOn: Boolean,
    scheduledOnDaysOfWeek: Collection<DayOfWeek>,
    scheduledOnDates: Collection<LocalDate>,
    offOnDates: Collection<LocalDate>,
  ) {
    val id =
      db.insertAlarm(
        fireAt = fireAt,
        name = name,
        isOn = isOn,
        scheduledOnDaysOfWeek = scheduledOnDaysOfWeek,
        scheduledOnDates = scheduledOnDates,
        offOnDates = offOnDates,
      )
    scheduler.scheduleAlarm(
      id = id,
      fireAt =
        LocalDateTime(
          date =
            if (fireAt < LocalTime.now()) {
              LocalDate.now().plus(1, DateTimeUnit.DAY)
            } else {
              LocalDate.now()
            },
          time = fireAt,
        ),
    )
  }

  override fun getAllAlarms(): Flow<List<AlarmListItem>> =
    db.selectAllAlarms().map { alarms ->
      alarms.map {
        AlarmListItem(
          fireAt = it.fireAt,
          name = it.name,
          isOn = it.isOn == 1L,
          nextScheduledOn = it.nextScheduledOn(),
        )
      }
    }

  private fun Alarm.nextScheduledOn(): LocalDateTime? {
    if (isOn == 0L) return null

    val now = LocalDateTime.now()
    val parsedOffOnDays = offOnDates?.split(",")?.map(LocalDate.Companion::parse).orEmpty()

    fun DayOfWeek.nextScheduledDate(): LocalDate {
      var currentDate = now.date
      while (currentDate.dayOfWeek != this) {
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
      }
      while (currentDate.atTime(fireAt) < now || currentDate in parsedOffOnDays) {
        currentDate = currentDate.plus(1, DateTimeUnit.WEEK)
      }
      return currentDate
    }

    val nextScheduledOnDayOfWeek =
      scheduledOnDaysOfWeek
        ?.split(",")
        ?.map { DayOfWeek(isoDayNumber = it.toInt()) }
        ?.toSet()
        ?.minOfOrNull(DayOfWeek::nextScheduledDate)

    val nextScheduledOnDate =
      scheduledOnDates
        ?.split(",")
        ?.map(LocalDate.Companion::parse)
        ?.run { if (parsedOffOnDays.isEmpty()) this else filter { it !in parsedOffOnDays } }
        ?.min()

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
      fireAt < now.time -> {
        LocalDate.now().plus(1, DateTimeUnit.DAY)
      }
      else -> {
        LocalDate.now()
      }
    }.atTime(fireAt)
  }
}
