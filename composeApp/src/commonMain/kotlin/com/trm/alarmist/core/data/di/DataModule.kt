package com.trm.alarmist.core.data.di

import com.trm.alarmist.core.data.AlarmsLocalRepository
import com.trm.alarmist.core.domain.AlarmsRepository
import org.koin.dsl.module

val dataModule = module { factory<AlarmsRepository> { AlarmsLocalRepository(get()) } }
