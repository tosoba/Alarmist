package com.trm.alarmist.feature.alarms.di

import com.trm.alarmist.feature.alarms.AlarmsFeature
import org.koin.dsl.module

val alarmsModule = module { factory { AlarmsFeature(get()) } }
