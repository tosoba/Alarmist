package com.trm.alarmist.core.data

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.domain.AlarmsRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class AlarmsLocalRepository(private val db: AlarmistDatabase) : AlarmsRepository {
  override suspend fun addOneShotAlarm(fireAt: LocalTime, name: String?) {
    db.insertAlarm(
      name = name,
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
}
