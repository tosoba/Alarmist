package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.AndroidAlarmScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val systemModule: Module = module {
  factory<AlarmScheduler> { AndroidAlarmScheduler(androidContext()) }
}
