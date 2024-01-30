package com.trm.alarmist

import android.app.Application

class AlarmistApp : Application() {
  override fun onCreate() {
    super.onCreate()
    PlatformKoinInitializer(this)()
  }
}
