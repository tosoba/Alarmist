package com.trm.alarmist.feature.stopwatch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
actual fun StopwatchContent(modifier: Modifier, component: StopwatchComponent) {
  val provider = koinInject<StopwatchScreenProvider>()
  provider.Content(modifier, component)
}
