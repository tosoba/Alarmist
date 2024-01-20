package com.trm.alarmist.feature.group

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun GroupContent(modifier: Modifier = Modifier, component: GroupComponent) {
  Box(modifier = modifier) { Text("Group", modifier = Modifier.align(Alignment.Center)) }
}
