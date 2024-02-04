package com.trm.alarmist.core.data

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.db.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

class AlarmLocalRepository(
  private val db: AlarmistDatabase,
  private val scheduler: AlarmScheduler,
) : AlarmRepository {
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

  override fun getAllAlarms(): Flow<List<Alarm>> = db.selectAllAlarms()
}
