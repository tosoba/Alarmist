package com.trm.alarmist

import android.app.Application
import com.trm.alarmist.core.system.createAlarmNotificationChannel

class AlarmistApp : Application() {
  override fun onCreate() {
    super.onCreate()
    PlatformKoinInitializer(this)()
    createAlarmNotificationChannel()
  }
}
