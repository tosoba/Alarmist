package com.trm.alarmist.feature.alarms.groups

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AlarmGroupsContent(modifier: Modifier = Modifier, component: AlarmGroupsComponent) {
  Box(modifier = modifier) { Text("Groups", modifier = Modifier.align(Alignment.Center)) }
}
