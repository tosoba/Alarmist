package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.system.AlarmScheduler
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val systemModule: Module = module { factoryOf<AlarmScheduler>(::IosAlarmScheduler) }
