package com.trm.alarmist.feature.root

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.trm.alarmist.feature.alarm.AlarmContent
import com.trm.alarmist.feature.alarms.AlarmsContent
import com.trm.alarmist.feature.clock.ClockContent
import com.trm.alarmist.feature.group.GroupContent
import com.trm.alarmist.feature.root.ui.RootAppBar
import com.trm.alarmist.feature.stopwatch.StopwatchContent
import com.trm.alarmist.feature.timer.TimerContent
import kotlinx.coroutines.launch

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
          style = MaterialTheme.typography.headlineLarge,
        )
        NavigationDrawerItem(
          label = { Text(text = "Alarms") },
          selected = childStack.active.instance is RootComponent.Child.Alarms,
          onClick = {
            closeDrawer()
            component.onAlarmsDrawerItemClick()
          },
        )
        NavigationDrawerItem(
          label = { Text(text = "Clock") },
          selected = childStack.active.instance is RootComponent.Child.Clock,
          onClick = {
            closeDrawer()
            component.onClockDrawerItemClick()
          },
        )
        NavigationDrawerItem(
          label = { Text(text = "Timer") },
          selected = childStack.active.instance is RootComponent.Child.Timer,
          onClick = {
            closeDrawer()
            component.onTimerDrawerItemClick()
          },
        )
        NavigationDrawerItem(
          label = { Text(text = "Stopwatch") },
          selected = childStack.active.instance is RootComponent.Child.Stopwatch,
          onClick = {
            closeDrawer()
            component.onStopwatchDrawerItemClick()
          },
        )
      }
    },
  ) {
    Column(modifier = modifier) {
      RootAppBar(
        activeChild = childStack.active.instance,
        onBackClick = component::onBackClick,
        onMenuClick = ::openDrawer,
      )

      Children(
        modifier = Modifier.fillMaxWidth().weight(1f),
        stack = childStack,
        animation = stackAnimation(fade()),
      ) {
        when (val child = it.instance) {
          is RootComponent.Child.Alarm -> {
            val state by child.component.feature.state.collectAsState()
            AlarmContent(
              modifier = Modifier.fillMaxSize(),
              state = state,
              onNameChange = child.component.feature::onNameChange,
              onFireAtChange = child.component.feature::onFireAtChange,
              onDayOfWeekClick = child.component.feature::onDayOfWeekClick,
              onDateOnOffSwitchCheckedChange =
                child.component.feature::onDateOnOffSwitchCheckedChange,
              onDeleteOnAllDaysWeekClick = child.component.feature::onDeleteOnAllDaysWeekClick,
              onDeleteOnDateClick = child.component.feature::onDeleteOnDateClick,
              onScheduleOnDateClick = child.component.feature::onScheduleOnDateClick,
              onConfirmClick = child.component::onConfirmClick,
            )
          }
          is RootComponent.Child.Alarms -> {
            AlarmsContent(modifier = Modifier.fillMaxSize(), component = child.component)
          }
          is RootComponent.Child.Group -> {
            val state by child.component.feature.state.collectAsState()
            GroupContent(
              modifier = Modifier.fillMaxSize(),
              state = state,
              onNameChange = child.component.feature::onNameChange,
              onConfirmClick = child.component.feature::onConfirmClick,
            )
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
