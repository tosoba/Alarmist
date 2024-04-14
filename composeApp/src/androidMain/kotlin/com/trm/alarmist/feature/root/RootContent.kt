package com.trm.alarmist.feature.root

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.alarms
import alarmist.composeapp.generated.resources.app_name
import alarmist.composeapp.generated.resources.clock
import alarmist.composeapp.generated.resources.stopwatch
import alarmist.composeapp.generated.resources.timer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.trm.alarmist.feature.alarm.AlarmContent
import com.trm.alarmist.feature.alarms.AlarmsContent
import com.trm.alarmist.feature.clock.ClockContent
import com.trm.alarmist.feature.group.GroupContent
import com.trm.alarmist.feature.root.ui.RootAppBar
import com.trm.alarmist.feature.root.ui.RootDialog
import com.trm.alarmist.feature.stopwatch.StopwatchContent
import com.trm.alarmist.feature.timer.TimerContent
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
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
          modifier = Modifier.fillMaxWidth().padding(28.dp),
          text = stringResource(Res.string.app_name),
          style = MaterialTheme.typography.titleSmall,
        )

        NavigationDrawerItem(
          modifier = Modifier.padding(horizontal = 12.dp),
          icon = {
            Icon(
              imageVector = Icons.Default.Alarm,
              contentDescription = stringResource(Res.string.alarms),
            )
          },
          label = {
            Text(
              text = stringResource(Res.string.alarms),
              style = MaterialTheme.typography.labelLarge,
              fontWeight =
                if (childStack.isItemSelected<RootComponent.Child.Alarms>()) FontWeight.Medium
                else FontWeight.Normal,
            )
          },
          selected = childStack.isItemSelected<RootComponent.Child.Alarms>(),
          onClick = {
            closeDrawer()
            component.onAlarmsDrawerItemClick()
          },
        )
        NavigationDrawerItem(
          modifier = Modifier.padding(horizontal = 12.dp),
          icon = {
            Icon(
              imageVector = Icons.Default.MoreTime,
              contentDescription = stringResource(Res.string.clock),
            )
          },
          label = {
            Text(
              text = stringResource(Res.string.clock),
              style = MaterialTheme.typography.labelLarge,
              fontWeight =
                if (childStack.isItemSelected<RootComponent.Child.Clock>()) FontWeight.Medium
                else FontWeight.Normal,
            )
          },
          selected = childStack.isItemSelected<RootComponent.Child.Clock>(),
          onClick = {
            closeDrawer()
            component.onClockDrawerItemClick()
          },
        )
        NavigationDrawerItem(
          modifier = Modifier.padding(horizontal = 12.dp),
          icon = {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = stringResource(Res.string.timer),
            )
          },
          label = {
            Text(
              text = stringResource(Res.string.timer),
              style = MaterialTheme.typography.labelLarge,
              fontWeight =
                if (childStack.isItemSelected<RootComponent.Child.Timer>()) FontWeight.Medium
                else FontWeight.Normal,
            )
          },
          selected = childStack.isItemSelected<RootComponent.Child.Timer>(),
          onClick = {
            closeDrawer()
            component.onTimerDrawerItemClick()
          },
        )
        NavigationDrawerItem(
          modifier = Modifier.padding(horizontal = 12.dp),
          icon = {
            Icon(
              imageVector = Icons.Default.Timelapse,
              contentDescription = stringResource(Res.string.stopwatch),
            )
          },
          label = {
            Text(
              text = stringResource(Res.string.stopwatch),
              style = MaterialTheme.typography.labelLarge,
              fontWeight =
                if (childStack.isItemSelected<RootComponent.Child.Stopwatch>()) FontWeight.Medium
                else FontWeight.Normal,
            )
          },
          selected = childStack.isItemSelected<RootComponent.Child.Stopwatch>(),
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
        onDeleteActionClick = component::onDeleteActionClick,
      )

      Children(
        modifier = Modifier.fillMaxWidth().weight(1f),
        stack = childStack,
        animation = stackAnimation(fade()),
      ) {
        when (val child = it.instance) {
          is RootComponent.Child.Alarm -> {
            val state by child.component.feature.state.collectAsState()
            val groups by child.component.feature.groups.collectAsState()
            AlarmContent(
              modifier = Modifier.fillMaxSize(),
              state = state,
              groups = groups,
              onNameChange = child.component.feature::onNameChange,
              onFireAtChange = child.component.feature::onFireAtChange,
              onDayOfWeekClick = child.component.feature::onDayOfWeekClick,
              onDateOnOffSwitchCheckedChange =
                child.component.feature::onDateOnOffSwitchCheckedChange,
              onDeleteOnAllDaysWeekClick = child.component.feature::onDeleteOnAllDaysWeekClick,
              onDeleteOnDateClick = child.component.feature::onDeleteOnDateClick,
              onScheduleOnDateClick = child.component.feature::onScheduleOnDateClick,
              onGroupClick = child.component.feature::onGroupClick,
              onSnoozeDurationChange = child.component.feature::onSnoozeDurationChange,
              onSnoozeLimitChange = child.component.feature::onSnoozeLimitChange,
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
              mode = child.component.mode,
              state = state,
              onNameChange = child.component.feature::onNameChange,
              onColorChange = child.component.feature::onColorChange,
              onToggleAlarmSelection = child.component.feature::onToggleAlarmSelection,
              onConfirmClick = child.component::onConfirmClick,
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

    val dialog by component.dialog.subscribeAsState()
    dialog.child?.instance?.let { RootDialog(it) }
  }
}

private inline fun <reified T> ChildStack<*, *>.isItemSelected(): Boolean = active.instance is T
