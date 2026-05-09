package com.trm.alarmist.app

import android.app.Application
import com.trm.alarmist.app.core.system.di.appSystemModule
import com.trm.alarmist.core.common.util.initNapierDebug
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.app.core.system.alarm.createAlarmNotificationChannels
import com.trm.alarmist.PlatformKoinInitializer
import org.koin.android.ext.android.get

class AlarmistApp : Application() {
  override fun onCreate() {
    super.onCreate()
    initNapierDebug()
    PlatformKoinInitializer(this).invoke(listOf(appSystemModule))
    createAlarmNotificationChannels()
    get<AlarmScheduler>().scheduleNextWidgetUpdate()
  }
}
