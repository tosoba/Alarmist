package com.trm.alarmist.feature.root.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.group.GroupComponent
import com.trm.alarmist.feature.root.RootComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootAppBar(activeChild: RootComponent.Child, onBackClick: () -> Unit, onMenuClick: () -> Unit) {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text =
          when (activeChild) {
            is RootComponent.Child.Alarms -> {
              "Alarms"
            }
            is RootComponent.Child.Alarm -> {
              when (activeChild.component.mode) {
                AlarmComponent.Mode.Add -> "New alarm"
                is AlarmComponent.Mode.Edit -> "Edit alarm"
              }
            }
            is RootComponent.Child.Group -> {
              when (activeChild.component.mode) {
                GroupComponent.Mode.Add -> "New group"
                is GroupComponent.Mode.Edit -> "Edit group"
              }
            }
            is RootComponent.Child.Clock -> {
              "Clock"
            }
            is RootComponent.Child.Timer -> {
              "Timer"
            }
            is RootComponent.Child.Stopwatch -> {
              "Stopwatch"
            }
          }
      )
    },
    navigationIcon = {
      IconButton(
        onClick = {
          when (activeChild) {
            is RootComponent.Child.Alarms,
            is RootComponent.Child.Clock,
            is RootComponent.Child.Timer,
            is RootComponent.Child.Stopwatch -> onMenuClick()
            else -> onBackClick()
          }
        }
      ) {
        when (activeChild) {
          is RootComponent.Child.Alarms,
          is RootComponent.Child.Clock,
          is RootComponent.Child.Timer,
          is RootComponent.Child.Stopwatch -> {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
          }
          else -> {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
          }
        }
      }
    },
  )
}
