package com.trm.alarmist.feature.timer

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TimerContent(modifier: Modifier = Modifier, component: TimerComponent) {
  Box(modifier = modifier) { Text("Timer", modifier = Modifier.align(Alignment.Center)) }
}
