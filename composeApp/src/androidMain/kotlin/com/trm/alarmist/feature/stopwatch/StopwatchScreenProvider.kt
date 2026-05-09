package com.trm.alarmist.feature.stopwatch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface StopwatchScreenProvider {
    @Composable
    fun Content(modifier: Modifier, component: StopwatchComponent)
}
