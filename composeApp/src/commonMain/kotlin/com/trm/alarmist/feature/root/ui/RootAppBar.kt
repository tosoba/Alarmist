package com.trm.alarmist.feature.root.ui

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarms
import alarmist.composeapp.generated.resources.back
import alarmist.composeapp.generated.resources.clock
import alarmist.composeapp.generated.resources.delete_alarm
import alarmist.composeapp.generated.resources.delete_group
import alarmist.composeapp.generated.resources.edit_alarm
import alarmist.composeapp.generated.resources.edit_group
import alarmist.composeapp.generated.resources.menu
import alarmist.composeapp.generated.resources.new_alarm
import alarmist.composeapp.generated.resources.new_group
import alarmist.composeapp.generated.resources.stopwatch
import alarmist.composeapp.generated.resources.timer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun RootAppBar(
  activeChild: RootComponent.Child,
  onBackClick: () -> Unit = {},
  onMenuClick: () -> Unit = {},
  onDeleteAlarmClick: () -> Unit = {},
  onDeleteGroupClick: () -> Unit = {},
) {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text =
          stringResource(
            when (activeChild) {
              is RootComponent.Child.Alarms -> {
                Res.string.alarms
              }
              is RootComponent.Child.Alarm -> {
                when (activeChild.component.mode) {
                  AlarmComponent.Mode.Add -> Res.string.new_alarm
                  is AlarmComponent.Mode.Edit -> Res.string.edit_alarm
                }
              }
              is RootComponent.Child.Group -> {
                when (activeChild.component.mode) {
                  GroupComponent.Mode.Add -> Res.string.new_group
                  is GroupComponent.Mode.Edit -> Res.string.edit_group
                }
              }
              is RootComponent.Child.Clock -> {
                Res.string.clock
              }
              is RootComponent.Child.Timer -> {
                Res.string.timer
              }
              is RootComponent.Child.Stopwatch -> {
                Res.string.stopwatch
              }
            }
          )
      )
    },
    actions = {
      when (activeChild) {
        is RootComponent.Child.Alarm -> {
          if (activeChild.component.mode is AlarmComponent.Mode.Edit) {
            DeleteActionButton(
              contentDescription = stringResource(Res.string.delete_alarm),
              onClick = onDeleteAlarmClick,
            )
          }
        }
        is RootComponent.Child.Group -> {
          if (activeChild.component.mode is GroupComponent.Mode.Edit) {
            DeleteActionButton(
              contentDescription = stringResource(Res.string.delete_group),
              onClick = onDeleteGroupClick,
            )
          }
        }
        else -> {
          return@CenterAlignedTopAppBar
        }
      }
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
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = stringResource(Res.string.menu),
            )
          }
          else -> {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.back),
            )
          }
        }
      }
    },
  )
}

@Composable
private fun DeleteActionButton(contentDescription: String, onClick: () -> Unit) {
  IconButton(onClick = onClick) {
    Icon(imageVector = Icons.Default.Delete, contentDescription = contentDescription)
  }
}
