package com.trm.alarmist.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class AndroidTimerScreenProvider : TimerScreenProvider {
  @Composable
  override fun Content(modifier: Modifier, component: TimerComponent) {
    AndroidTimerContent(modifier = modifier, component = component)
  }
}
