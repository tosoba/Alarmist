package com.trm.alarmist.core.database.di

import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.database.DriverFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

actual val databaseModule = module {
  single { AlarmistDatabase(DriverFactory(), Dispatchers.IO) }
}
