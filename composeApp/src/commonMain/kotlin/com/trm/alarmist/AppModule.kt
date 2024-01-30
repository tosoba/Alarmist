package com.trm.alarmist

import com.trm.alarmist.core.data.di.dataModule
import com.trm.alarmist.core.database.di.databaseModule
import org.koin.dsl.module

val appModule = module { includes(dataModule, databaseModule) }

interface KoinInitializer {
  operator fun invoke()
}

expect class PlatformKoinInitializer : KoinInitializer
