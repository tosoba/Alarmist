package com.trm.alarmist.feature.alarms.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AlarmsNavigationBar(selectedIndex: Int, onPageSelected: (Int) -> Unit) {
  NavigationBar(modifier = Modifier.fillMaxWidth()) {
    NavigationBarItem(
      selected = selectedIndex == 0,
      onClick = { onPageSelected(0) },
      icon = { Icon(imageVector = Icons.Default.List, contentDescription = "All") },
      label = { Text("All") },
    )

    NavigationBarItem(
      selected = selectedIndex == 1,
      onClick = { onPageSelected(1) },
      icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Upcoming") },
      label = { Text("Upcoming") },
    )

    NavigationBarItem(
      selected = selectedIndex == 2,
      onClick = { onPageSelected(2) },
      icon = { Icon(imageVector = Icons.Default.Menu, contentDescription = "Groups") },
      label = { Text("Groups") },
    )
  }
}
