package com.trm.alarmist.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.alarm.DefaultAlarmComponent
import com.trm.alarmist.feature.alarms.AlarmsComponent
import com.trm.alarmist.feature.alarms.DefaultAlarmsComponent
import com.trm.alarmist.feature.group.DefaultGroupComponent
import com.trm.alarmist.feature.group.GroupComponent
import kotlinx.serialization.Serializable

interface RootComponent {

  val childStack: Value<ChildStack<*, Child>>

  fun onBackClicked()

  sealed interface Child {
    class Alarms(val component: AlarmsComponent) : Child

    class Alarm(val component: AlarmComponent) : Child

    class Group(val component: GroupComponent) : Child
  }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {
  private val navigation = StackNavigation<Config>()

  override val childStack: Value<ChildStack<*, RootComponent.Child>> =
      childStack(
          source = navigation,
          serializer = Config.serializer(),
          initialConfiguration = Config.Alarms,
          handleBackButton = true,
          childFactory = ::createChild,
      )

  private fun createChild(config: Config, componentContext: ComponentContext): RootComponent.Child =
      when (config) {
        is Config.Alarm -> {
          RootComponent.Child.Alarm(DefaultAlarmComponent(config.mode, componentContext))
        }
        Config.Alarms -> {
          RootComponent.Child.Alarms(DefaultAlarmsComponent(componentContext))
        }
        is Config.Group -> {
          RootComponent.Child.Group(DefaultGroupComponent(config.mode, componentContext))
        }
      }

  override fun onBackClicked() {}

  @Serializable
  private sealed interface Config {
    @Serializable data object Alarms : Config

    @Serializable data class Alarm(val mode: AlarmComponent.Mode) : Config

    @Serializable data class Group(val mode: GroupComponent.Mode) : Config
  }
}
