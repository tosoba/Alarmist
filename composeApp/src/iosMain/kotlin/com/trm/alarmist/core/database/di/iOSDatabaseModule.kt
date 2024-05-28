package com.trm.alarmist.core.database.di

import com.trm.alarmist.core.database.SqlDriverFactory
import com.trm.alarmist.core.database.adapter.alarmAdapter
import com.trm.alarmist.db.AlarmistDb
import org.koin.dsl.module

actual val databaseModule = module {
  single { AlarmistDb(driver = SqlDriverFactory().createDriver(), alarmAdapter = alarmAdapter()) }
}
