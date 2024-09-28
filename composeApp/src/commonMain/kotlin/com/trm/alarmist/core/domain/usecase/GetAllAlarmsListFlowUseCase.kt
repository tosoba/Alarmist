package com.trm.alarmist.core.domain.usecase

import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.common.util.toListModel
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.core.domain.model.AlarmListModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime

class GetAllAlarmsListFlowUseCase(private val repository: AlarmRepository) {
  operator fun invoke(): Flow<List<AlarmListModel>> =
    repository.getAllAlarmsListFlow().map { alarms ->
      val now = LocalDateTime.now()
      alarms.map { alarm -> alarm.toListModel(now) }
    }
}
