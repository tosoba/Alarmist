package com.trm.alarmist.core

import com.trm.alarmist.core.data.di.dataModule
import com.trm.alarmist.core.database.di.databaseModule
import org.koin.dsl.module

val coreModule = module { includes(dataModule, databaseModule) }
