package com.trm.alarmist.feature.alarms.ui

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.all
import alarmist.composeapp.generated.resources.groups
import alarmist.composeapp.generated.resources.upcoming
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlarmsNavigationBar(
  selectedIndex: Int,
  onPageSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  NavigationBar(modifier = modifier) {
    NavigationBarItem(
      selected = selectedIndex == 0,
      onClick = { onPageSelected(0) },
      icon = {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.List,
          contentDescription = stringResource(Res.string.all),
        )
      },
      label = { Text(stringResource(Res.string.all)) },
    )

    NavigationBarItem(
      selected = selectedIndex == 1,
      onClick = { onPageSelected(1) },
      icon = {
        Icon(
          imageVector = Icons.Default.Notifications,
          contentDescription = stringResource(Res.string.upcoming),
        )
      },
      label = { Text(stringResource(Res.string.upcoming)) },
    )

    NavigationBarItem(
      selected = selectedIndex == 2,
      onClick = { onPageSelected(2) },
      icon = {
        Icon(
          imageVector = Icons.Default.Menu,
          contentDescription = stringResource(Res.string.groups),
        )
      },
      label = { Text(stringResource(Res.string.groups)) },
    )
  }
}
