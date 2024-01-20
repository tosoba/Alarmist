package com.trm.alarmist.feature.alarms.list

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlarmListContent(modifier: Modifier = Modifier, component: AlarmListComponent) {
  Box(modifier = modifier) { Text("List", modifier = Modifier.align(Alignment.Center)) }
}
