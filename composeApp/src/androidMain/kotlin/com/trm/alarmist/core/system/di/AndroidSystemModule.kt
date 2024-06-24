package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.alarm.AndroidAlarmScheduler
import com.trm.alarmist.core.system.AndroidWidgetManager
import com.trm.alarmist.core.system.WidgetManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val systemModule: Module = module {
  factory<AlarmScheduler> { AndroidAlarmScheduler(androidContext()) }
  factory<WidgetManager> { AndroidWidgetManager(androidContext()) }
}
