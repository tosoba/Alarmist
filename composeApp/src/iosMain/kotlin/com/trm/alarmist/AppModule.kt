package com.trm.alarmist

import org.koin.core.context.startKoin

actual class PlatformKoinInitializer : KoinInitializer {
  override operator fun invoke() {
    startKoin { modules(appModule) }
  }
}
