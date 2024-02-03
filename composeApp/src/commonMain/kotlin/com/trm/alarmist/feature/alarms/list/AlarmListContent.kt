package com.trm.alarmist.feature.alarms.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun AlarmListContent(modifier: Modifier = Modifier, component: AlarmListComponent) {
  val alarms by component.alarms.collectAsState(emptyList())
  LazyColumn(modifier = modifier) { items(alarms) { Text(it.fireAt.toString()) } }
}
