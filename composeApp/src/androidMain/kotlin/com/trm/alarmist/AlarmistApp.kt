package com.trm.alarmist

import android.app.Application
import com.trm.alarmist.core.common.util.initNapierDebug
import com.trm.alarmist.core.system.alarm.createAlarmNotificationChannels
import com.trm.alarmist.widget.WidgetUpdateWorker

class AlarmistApp : Application() {
  override fun onCreate() {
    super.onCreate()
    initNapierDebug()
    PlatformKoinInitializer(this)()
    createAlarmNotificationChannels()
    WidgetUpdateWorker.enqueue(this)
  }
}
