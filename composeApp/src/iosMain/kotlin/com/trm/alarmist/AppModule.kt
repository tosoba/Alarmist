package com.trm.alarmist

import org.koin.core.context.startKoin
import org.koin.core.module.Module

actual class PlatformKoinInitializer : KoinInitializer {
  override operator fun invoke(additionalModules: List<Module>) {
    startKoin { modules(appModule + additionalModules) }
  }
}
