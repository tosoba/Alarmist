package com.trm.alarmist.core.domain.di

import com.trm.alarmist.core.domain.usecase.AddAlarmUseCase
import com.trm.alarmist.core.domain.usecase.DeleteAlarmUseCase
import com.trm.alarmist.core.domain.usecase.EditAlarmUseCase
import com.trm.alarmist.core.domain.usecase.GetAlarmsInGroupFlowUseCase
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledOnDateFlowUseCase
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledTodayUseCase
import com.trm.alarmist.core.domain.usecase.GetAndResetMissedAlarmsOnBootUseCase
import com.trm.alarmist.core.domain.usecase.GetGroupedAlarmsFlowUseCase
import com.trm.alarmist.core.domain.usecase.GetScheduledAlarmCountsForDateRangeUseCase
import com.trm.alarmist.core.domain.usecase.IsAlarmScheduledToFireAtDateTime
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import com.trm.alarmist.core.domain.usecase.ToggleUpcomingAlarmOnOffOnDateUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnSnoozeUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmScheduleUseCase
import com.trm.alarmist.core.domain.usecase.UpdateGroupOnOffUseCase
import org.koin.dsl.module

val domainModule = module {
  factory { AddAlarmUseCase(get(), get()) }
  factory { EditAlarmUseCase(get(), get()) }
  factory { DeleteAlarmUseCase(get(), get()) }
  factory { ToggleAlarmOnOffUseCase(get(), get()) }
  factory { UpdateGroupOnOffUseCase(get(), get()) }
  factory { UpdateAlarmScheduleUseCase(get()) }
  factory { UpdateAlarmOnDismissUseCase(get(), get()) }
  factory { UpdateAlarmOnSnoozeUseCase(get(), get()) }
  factory { GetGroupedAlarmsFlowUseCase(get()) }
  factory { GetAlarmsInGroupFlowUseCase(get()) }
  factory { GetAndResetMissedAlarmsOnBootUseCase(get()) }
  factory { GetAlarmsScheduledOnDateFlowUseCase(get()) }
  factory { GetAlarmsScheduledTodayUseCase(get()) }
  factory { GetScheduledAlarmCountsForDateRangeUseCase(get()) }
  factory { IsAlarmScheduledToFireAtDateTime(get()) }
  factory { ToggleUpcomingAlarmOnOffOnDateUseCase(get(), get(), get()) }
}
