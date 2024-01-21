package com.trm.alarmist.feature.root

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.alarm.AlarmContent
import com.trm.alarmist.feature.alarms.AlarmsContent
import com.trm.alarmist.feature.group.GroupComponent
import com.trm.alarmist.feature.group.GroupContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootContent(modifier: Modifier = Modifier, component: RootComponent) {
  val childStack by component.childStack.subscribeAsState()

  Column(modifier = modifier) {
    CenterAlignedTopAppBar(
      title = {
        Text(
          text =
            when (val active = childStack.active.instance) {
              is RootComponent.Child.Alarm ->
                when (active.component.mode) {
                  AlarmComponent.Mode.Add -> "New alarm"
                  AlarmComponent.Mode.Edit -> "Edit alarm"
                }
              is RootComponent.Child.Alarms -> "Alarms"
              is RootComponent.Child.Group ->
                when (active.component.mode) {
                  GroupComponent.Mode.Add -> "New group"
                  GroupComponent.Mode.Edit -> "Edit group"
                }
            }
        )
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
      }
    }
  }
}
