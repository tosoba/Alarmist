package com.trm.alarmist.core.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

internal actual val databaseModule = module {
  single { AlarmistDatabase(DriverFactory(), Dispatchers.IO) }
}
