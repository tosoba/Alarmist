package com.trm.alarmist.core.database

import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal actual val databaseModule = module {
  single { AlarmistDatabase(DriverFactory(androidContext()), Dispatchers.IO) }
}
