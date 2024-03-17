package com.trm.alarmist.core.domain.di

import com.trm.alarmist.core.domain.usecase.AddAlarmUseCase
import com.trm.alarmist.core.domain.usecase.EditAlarmUseCase
import com.trm.alarmist.core.domain.usecase.GetAlarmsInGroupFlowUseCase
import com.trm.alarmist.core.domain.usecase.GetAlarmsScheduledOnDateUseCase
import com.trm.alarmist.core.domain.usecase.GetAndUpdateOnAlarmsWithMissedTimestampsOnBootUseCase
import com.trm.alarmist.core.domain.usecase.GetGroupedAlarmsUseCase
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnNotificationUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmScheduleUseCase
import com.trm.alarmist.core.domain.usecase.UpdateGroupOnOffUseCase
import org.koin.dsl.module

val domainModule = module {
  factory { AddAlarmUseCase(get(), get()) }
  factory { EditAlarmUseCase(get(), get()) }
  factory { ToggleAlarmOnOffUseCase(get(), get()) }
  factory { UpdateGroupOnOffUseCase(get(), get()) }
  factory { UpdateAlarmScheduleUseCase(get()) }
  factory { UpdateAlarmOnNotificationUseCase(get(), get()) }
  factory { GetGroupedAlarmsUseCase(get()) }
  factory { GetAlarmsInGroupFlowUseCase(get()) }
  factory { GetAndUpdateOnAlarmsWithMissedTimestampsOnBootUseCase(get()) }
  factory { GetAlarmsScheduledOnDateUseCase(get()) }
}
