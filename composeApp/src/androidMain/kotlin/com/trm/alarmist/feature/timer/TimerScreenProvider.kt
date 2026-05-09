package com.trm.alarmist.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface TimerScreenProvider {
    @Composable
    fun Content(modifier: Modifier, component: TimerComponent)
}
