package com.trm.alarmist.feature.stopwatch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class AndroidStopwatchScreenProvider : StopwatchScreenProvider {
  @Composable
  override fun Content(modifier: Modifier, component: StopwatchComponent) {
    AndroidStopwatchContent(modifier, component)
  }
}
