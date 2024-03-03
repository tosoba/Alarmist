package com.trm.alarmist.core.common.util

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun initNapierDebug() {
  Napier.base(DebugAntilog())
}
