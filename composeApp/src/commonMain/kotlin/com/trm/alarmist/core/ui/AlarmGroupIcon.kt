package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AlarmGroupIcon(color: Long, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    Icon(
      imageVector = Icons.Default.Folder,
      contentDescription = null,
      modifier = Modifier.size(35.dp).align(Alignment.Center),
    )
    Icon(
      imageVector = Icons.Default.Folder,
      contentDescription = null,
      modifier = Modifier.size(34.dp).align(Alignment.Center),
      tint = Color(color),
    )
  }
}
