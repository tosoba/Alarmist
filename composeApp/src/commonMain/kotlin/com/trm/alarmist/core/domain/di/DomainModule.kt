package com.trm.alarmist.core.domain.di

import com.trm.alarmist.core.domain.usecase.AddAlarmUseCase
import com.trm.alarmist.core.domain.usecase.EditAlarmUseCase
import com.trm.alarmist.core.domain.usecase.ToggleAlarmOnOffUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnDismissedUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmOnFiredUseCase
import com.trm.alarmist.core.domain.usecase.UpdateAlarmScheduleUseCase
import org.koin.dsl.module

val domainModule = module {
  factory { AddAlarmUseCase(get(), get()) }
  factory { EditAlarmUseCase(get(), get()) }
  factory { ToggleAlarmOnOffUseCase(get(), get()) }
  factory { UpdateAlarmScheduleUseCase(get()) }
  factory { UpdateAlarmOnFiredUseCase(get()) }
  factory { UpdateAlarmOnDismissedUseCase(get()) }
}
