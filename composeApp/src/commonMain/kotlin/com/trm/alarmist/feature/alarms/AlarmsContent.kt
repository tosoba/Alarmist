package com.trm.alarmist.feature.alarms

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlarmsContent(modifier: Modifier = Modifier, component: AlarmsComponent) {
  Box(modifier = modifier) { Text("Alarms", modifier = Modifier.align(Alignment.Center)) }
}
