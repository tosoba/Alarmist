package com.trm.alarmist.feature.alarm.di

import com.trm.alarmist.feature.alarm.AlarmFeature
import org.koin.dsl.module

val alarmModule = module { factory { AlarmFeature(get()) } }
