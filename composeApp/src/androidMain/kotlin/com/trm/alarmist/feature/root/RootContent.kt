package com.trm.alarmist.feature.root

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.alarm.AlarmContent
import com.trm.alarmist.feature.alarms.AlarmsContent
import com.trm.alarmist.feature.clock.ClockContent
import com.trm.alarmist.feature.group.GroupComponent
import com.trm.alarmist.feature.group.GroupContent
import com.trm.alarmist.feature.stopwatch.StopwatchContent
import com.trm.alarmist.feature.timer.TimerContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootContent(modifier: Modifier = Modifier, component: RootComponent) {
  val childStack by component.childStack.subscribeAsState()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  fun openDrawer() {
    scope.launch { drawerState.open() }
  }

  fun closeDrawer() {
    scope.launch { drawerState.close() }
  }

  BackHandler(enabled = drawerState.isOpen, onBack = ::closeDrawer)

  ModalNavigationDrawer(
    drawerState = drawerState,
    gesturesEnabled = drawerState.isOpen,
    drawerContent = {
      ModalDrawerSheet {
        Text(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          text = "Alarmist",
          style = MaterialTheme.typography.headlineLarge
        )
        NavigationDrawerItem(
          label = { Text(text = "Alarms") },
          selected = childStack.active.instance is RootComponent.Child.Alarms,
          onClick = {
            closeDrawer()
            component.onAlarmsDrawerItemClick()
          }
        )
        NavigationDrawerItem(
          label = { Text(text = "Clock") },
          selected = childStack.active.instance is RootComponent.Child.Clock,
          onClick = {
            closeDrawer()
            component.onClockDrawerItemClick()
          }
        )
        NavigationDrawerItem(
          label = { Text(text = "Timer") },
          selected = childStack.active.instance is RootComponent.Child.Timer,
          onClick = {
            closeDrawer()
            component.onTimerDrawerItemClick()
          }
        )
        NavigationDrawerItem(
          label = { Text(text = "Stopwatch") },
          selected = childStack.active.instance is RootComponent.Child.Stopwatch,
          onClick = {
            closeDrawer()
            component.onStopwatchDrawerItemClick()
          }
        )
      }
    }
  ) {
    Column(modifier = modifier) {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text =
              when (val active = childStack.active.instance) {
                is RootComponent.Child.Alarms -> {
                  "Alarms"
                }
                is RootComponent.Child.Alarm -> {
                  when (active.component.mode) {
                    AlarmComponent.Mode.Add -> "New alarm"
                    AlarmComponent.Mode.Edit -> "Edit alarm"
                  }
                }
                is RootComponent.Child.Group -> {
                  when (active.component.mode) {
                    GroupComponent.Mode.Add -> "New group"
                    GroupComponent.Mode.Edit -> "Edit group"
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
              when (childStack.active.instance) {
                is RootComponent.Child.Alarms,
                is RootComponent.Child.Clock,
                is RootComponent.Child.Timer,
                is RootComponent.Child.Stopwatch -> openDrawer()
                else -> component.onBackClick()
              }
            }
          ) {
            when (childStack.active.instance) {
              is RootComponent.Child.Alarms,
              is RootComponent.Child.Clock,
              is RootComponent.Child.Timer,
              is RootComponent.Child.Stopwatch -> {
                Icon(
                  imageVector = Icons.Default.Menu,
                  contentDescription = "Menu",
                )
              }
              else -> {
                Icon(
                  imageVector = Icons.Default.ArrowBack,
                  contentDescription = "Back",
                )
              }
            }
          }
        }
      )

      Children(
        modifier = Modifier.fillMaxWidth().weight(1f),
        stack = childStack,
        animation = stackAnimation(),
      ) {
        when (val child = it.instance) {
          is RootComponent.Child.Alarm -> {
            AlarmContent(modifier = Modifier.fillMaxSize(), component = child.component)
          }
          is RootComponent.Child.Alarms -> {
            AlarmsContent(modifier = Modifier.fillMaxSize(), component = child.component)
          }
          is RootComponent.Child.Group -> {
            GroupContent(modifier = Modifier.fillMaxSize(), component = child.component)
          }
          is RootComponent.Child.Clock -> {
            ClockContent(modifier = Modifier.fillMaxSize(), component = child.component)
          }
          is RootComponent.Child.Stopwatch -> {
            StopwatchContent(modifier = Modifier.fillMaxSize(), component = child.component)
          }
          is RootComponent.Child.Timer -> {
            TimerContent(modifier = Modifier.fillMaxSize(), component = child.component)
          }
        }
      }
    }
  }
}
