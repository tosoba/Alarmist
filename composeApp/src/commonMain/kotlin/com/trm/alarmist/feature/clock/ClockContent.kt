package com.trm.alarmist.feature.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ClockContent(modifier: Modifier = Modifier, component: ClockComponent) {
  Box(modifier = modifier) { Text("Clock", modifier = Modifier.align(Alignment.Center)) }
}
