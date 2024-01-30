package com.trm.alarmist

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

actual class PlatformKoinInitializer(private val context: Context) : KoinInitializer {
  override operator fun invoke() {
    startKoin {
      androidContext(context)
      modules(appModule)
    }
  }
}
