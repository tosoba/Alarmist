package com.trm.alarmist

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module

actual class PlatformKoinInitializer(private val context: Context) : KoinInitializer {
  override operator fun invoke(additionalModules: List<Module>) {
    startKoin {
      androidContext(context)
      modules(appModule + additionalModules)
    }
  }
}
