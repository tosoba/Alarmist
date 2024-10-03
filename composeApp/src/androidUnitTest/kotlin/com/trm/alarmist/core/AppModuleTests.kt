package com.trm.alarmist.core

import com.trm.alarmist.appModule
import kotlin.test.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class AppModuleTests {
  @Test
  fun `appModule should be verified positively`() {
    appModule.verify()
  }
}
