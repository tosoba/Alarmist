package com.trm.alarmist

import com.trm.alarmist.core.data.di.dataModule
import com.trm.alarmist.core.database.di.databaseModule
import com.trm.alarmist.core.system.di.systemModule
import org.koin.dsl.module

val appModule = module { includes(dataModule, databaseModule, systemModule) }

interface KoinInitializer {
  operator fun invoke()
}

expect class PlatformKoinInitializer : KoinInitializer
