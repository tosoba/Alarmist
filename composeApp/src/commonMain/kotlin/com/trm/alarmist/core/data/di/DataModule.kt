package com.trm.alarmist.core.data.di

import com.trm.alarmist.core.data.AlarmLocalRepository
import com.trm.alarmist.core.domain.AlarmRepository
import com.trm.alarmist.db.AlarmistDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val dataModule = module {
  factory<AlarmRepository> {
    AlarmLocalRepository(queries = get<AlarmistDb>().alarmistQueries, dispatcher = Dispatchers.IO)
  }
}
