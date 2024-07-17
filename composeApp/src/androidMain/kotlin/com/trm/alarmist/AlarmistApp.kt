package com.trm.alarmist

import android.app.Application
import com.trm.alarmist.core.common.util.initNapierDebug
import com.trm.alarmist.core.system.AlarmScheduler
import com.trm.alarmist.core.system.alarm.createAlarmNotificationChannels
import org.koin.android.ext.android.get

class AlarmistApp : Application() {
  override fun onCreate() {
    super.onCreate()
    initNapierDebug()
    PlatformKoinInitializer(this)()
    createAlarmNotificationChannels()
    get<AlarmScheduler>().scheduleNextWidgetUpdate()
  }
}
