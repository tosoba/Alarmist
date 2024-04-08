package com.trm.alarmist.feature.alarm

import androidx.compose.runtime.Immutable
import com.trm.alarmist.core.common.util.nextFullHour
import com.trm.alarmist.core.common.util.now
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.AlarmModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AlarmState(
  val fireAtTime: LocalTime = LocalTime(now().nextFullHour(), 0),
  val groupId: Long = AlarmGroupModel.UNGROUPED_ID,
  val name: String? = null,
  val scheduledOnDaysOfWeek: Set<DayOfWeek> = emptySet(),
  val scheduledOnDates: Set<LocalDate> = emptySet(),
  val offOnDates: Set<LocalDate> = emptySet(),
  val snoozeDuration: AlarmSnoozeDuration = AlarmSnoozeDuration.ZERO,
) {
  constructor(
    alarm: AlarmModel
  ) : this(
    fireAtTime = alarm.fireAtTime,
    groupId = alarm.groupId ?: AlarmGroupModel.UNGROUPED_ID,
    name = alarm.name,
    scheduledOnDaysOfWeek = alarm.scheduledOnDaysOfWeek.toSet(),
    scheduledOnDates = alarm.scheduledOnDates.toSet(),
    offOnDates = alarm.offOnDates.toSet(),
    // TODO: snoozeDuration from alarm
  )

  constructor(
    alarm: AlarmListModel
  ) : this(
    fireAtTime = alarm.fireAtTime,
    groupId = alarm.groupId ?: AlarmGroupModel.UNGROUPED_ID,
    name = alarm.name,
  )
}
