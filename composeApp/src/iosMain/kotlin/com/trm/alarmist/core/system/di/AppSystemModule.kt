package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.system.notification.IosTimerNotifications
import com.trm.alarmist.core.system.timer.IosTimerController
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appSystemModule: Module = module {
  singleOf(::IosTimerNotifications)
  singleOf(::IosTimerController)
}
