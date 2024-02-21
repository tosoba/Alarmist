package com.trm.alarmist.core.database.di

import com.trm.alarmist.core.database.SqlDriverFactory
import com.trm.alarmist.core.database.adapter.LocalTimeAdapter
import com.trm.alarmist.db.Alarm
import com.trm.alarmist.db.AlarmistDb
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val databaseModule = module {
  single {
    AlarmistDb(
      driver = SqlDriverFactory(androidContext()).createDriver(),
      alarmAdapter = Alarm.Adapter(LocalTimeAdapter),
    )
  }
}
