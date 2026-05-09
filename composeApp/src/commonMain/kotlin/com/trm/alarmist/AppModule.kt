package com.trm.alarmist

import com.trm.alarmist.core.data.di.dataModule
import com.trm.alarmist.core.database.di.databaseModule
import com.trm.alarmist.core.domain.di.domainModule
import com.trm.alarmist.core.system.di.systemModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module { includes(domainModule, dataModule, databaseModule, systemModule) }

interface KoinInitializer {
  operator fun invoke(additionalModules: List<Module> = emptyList())
}

expect class PlatformKoinInitializer : KoinInitializer
