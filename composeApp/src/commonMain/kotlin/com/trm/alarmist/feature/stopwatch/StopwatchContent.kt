package com.trm.alarmist.feature.stopwatch

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun StopwatchContent(modifier: Modifier = Modifier, component: StopwatchComponent) {
  Box(modifier = modifier) { Text("Stopwatch", modifier = Modifier.align(Alignment.Center)) }
}
