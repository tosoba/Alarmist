package com.trm.alarmist.feature.root.ui

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarms
import alarmist.composeapp.generated.resources.clock
import alarmist.composeapp.generated.resources.menu
import alarmist.composeapp.generated.resources.stopwatch
import alarmist.composeapp.generated.resources.timer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.trm.alarmist.feature.root.RootComponent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun RootAppBar(activeChild: RootComponent.Child, onMenuClick: () -> Unit = {}) {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text =
          stringResource(
            when (activeChild) {
              is RootComponent.Child.Alarms -> Res.string.alarms
              is RootComponent.Child.Clock -> Res.string.clock
              is RootComponent.Child.Timer -> Res.string.timer
              is RootComponent.Child.Stopwatch -> Res.string.stopwatch
            }
          )
      )
    },
    navigationIcon = {
      IconButton(onClick = onMenuClick) {
        Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(Res.string.menu))
      }
    },
  )
}
