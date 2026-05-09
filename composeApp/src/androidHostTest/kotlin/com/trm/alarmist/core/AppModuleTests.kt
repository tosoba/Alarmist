package com.trm.alarmist.core

import com.trm.alarmist.appModule
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import kotlin.test.Test

@OptIn(KoinExperimentalAPI::class)
class AppModuleTests {
  @Test
  fun `appModule should be verified positively`() {
    appModule.verify()
  }
}
