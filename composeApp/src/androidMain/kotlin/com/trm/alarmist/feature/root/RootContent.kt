package com.trm.alarmist.feature.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.trm.alarmist.feature.alarm.AlarmContent
import com.trm.alarmist.feature.alarms.AlarmsContent
import com.trm.alarmist.feature.group.GroupContent

@Composable
fun RootContent(modifier: Modifier = Modifier, component: RootComponent) {
  val childStack by component.childStack.subscribeAsState()

  Children(
      modifier = modifier,
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
