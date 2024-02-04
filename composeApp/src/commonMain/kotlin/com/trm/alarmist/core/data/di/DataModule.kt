package com.trm.alarmist.core.data.di

import com.trm.alarmist.core.data.AlarmLocalRepository
import com.trm.alarmist.core.domain.AlarmRepository
import org.koin.dsl.module

val dataModule = module { factory<AlarmRepository> { AlarmLocalRepository(get(), get()) } }
