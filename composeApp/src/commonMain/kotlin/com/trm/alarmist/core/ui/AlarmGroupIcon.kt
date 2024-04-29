package com.trm.alarmist.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun AlarmGroupIcon(color: Long, size: Dp, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    Icon(
      imageVector = Icons.Default.Folder,
      contentDescription = null,
      modifier = Modifier.size(size).align(Alignment.Center),
      tint = Color(color),
    )
    Icon(
      imageVector = Icons.Default.FolderOpen,
      contentDescription = null,
      modifier = Modifier.size(size).align(Alignment.Center),
    )
  }
}
