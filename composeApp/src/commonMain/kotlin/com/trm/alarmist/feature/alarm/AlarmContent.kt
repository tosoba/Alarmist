package com.trm.alarmist.feature.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlarmContent(modifier: Modifier = Modifier, component: AlarmComponent) {
  Box(modifier = modifier) { Text("Alarm", modifier = Modifier.align(Alignment.Center)) }
}
