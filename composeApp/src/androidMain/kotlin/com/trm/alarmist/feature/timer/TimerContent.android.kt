package com.trm.alarmist.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
actual fun TimerContent(modifier: Modifier, component: TimerComponent) {
    val provider = koinInject<TimerScreenProvider>()
    provider.Content(modifier, component)
}
