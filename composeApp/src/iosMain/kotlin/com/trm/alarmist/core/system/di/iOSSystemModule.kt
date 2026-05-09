package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.IosWidgetManager
import com.trm.alarmist.core.system.WidgetManager
import com.trm.alarmist.core.system.alarm.IosAlarmNotifications
import com.trm.alarmist.core.system.notification.IosTimerNotifications
import com.trm.alarmist.core.system.stopwatch.IosStopwatchController
import com.trm.alarmist.core.system.timer.IosTimerController
import org.koin.core.module.Module
import org.koin.dsl.module

actual val systemModule: Module = module {
  single { IosTimerNotifications() }
  single { IosTimerController(get()) }
  single { IosStopwatchController() }
  single { IosAlarmNotifications() }
  factory<AlarmScheduler> { IosAlarmScheduler(get()) }
  factory<WidgetManager> { IosWidgetManager() }
}
