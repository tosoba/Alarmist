package com.trm.alarmist.feature.alarms.upcoming

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun UpcomingAlarmsContent(modifier: Modifier = Modifier, component: UpcomingAlarmsComponent) {
  Box(modifier = modifier) { Text("Upcoming", modifier = Modifier.align(Alignment.Center)) }
}
