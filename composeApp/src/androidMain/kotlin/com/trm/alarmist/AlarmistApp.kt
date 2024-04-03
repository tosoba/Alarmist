package com.trm.alarmist

import android.app.Application
import com.trm.alarmist.core.common.util.initNapierDebug
import com.trm.alarmist.core.system.createAlarmNotificationChannels

class AlarmistApp : Application() {
  override fun onCreate() {
    super.onCreate()
    initNapierDebug()
    PlatformKoinInitializer(this)()
    createAlarmNotificationChannels()
  }
}
