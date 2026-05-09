package com.trm.alarmist.core.system.di

import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.AndroidWidgetManager
import com.trm.alarmist.core.system.WidgetManager
import com.trm.alarmist.core.system.alarm.AndroidAlarmScheduler
import com.trm.alarmist.feature.stopwatch.AndroidStopwatchScreenProvider
import com.trm.alarmist.feature.stopwatch.StopwatchScreenProvider
import com.trm.alarmist.feature.timer.AndroidTimerScreenProvider
import com.trm.alarmist.feature.timer.TimerScreenProvider
import com.trm.alarmist.feature.widgets.AndroidWidgetScreenProvider
import com.trm.alarmist.feature.widgets.WidgetScreenProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appSystemModule = module {
  factory<AlarmScheduler> { AndroidAlarmScheduler(androidContext()) }
  factory<WidgetManager> { AndroidWidgetManager(androidContext()) }
  factory<WidgetScreenProvider> { AndroidWidgetScreenProvider() }
  factory<StopwatchScreenProvider> { AndroidStopwatchScreenProvider() }
  factory<TimerScreenProvider> { AndroidTimerScreenProvider() }
}
