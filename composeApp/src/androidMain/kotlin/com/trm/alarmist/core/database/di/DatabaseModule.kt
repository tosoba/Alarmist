package com.trm.alarmist.core.database.di

import com.trm.alarmist.core.database.AlarmistDatabase
import com.trm.alarmist.core.database.DriverFactory
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val databaseModule = module {
  single { AlarmistDatabase(DriverFactory(androidContext()), Dispatchers.IO) }
}
