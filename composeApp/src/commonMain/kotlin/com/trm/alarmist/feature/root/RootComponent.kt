package com.trm.alarmist.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
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

  fun onAddAlarmClick()

  fun onEditAlarmClick()

  fun onAddGroupClick()

  fun onEditGroupClick()

  fun onBackClick()

  sealed interface Child {
    class Alarms(val component: AlarmsComponent) : Child

    class Alarm(val component: AlarmComponent) : Child

    class Group(val component: GroupComponent) : Child
  }
}

class DefaultRootComponent(
  componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {
  private val navigation = StackNavigation<ChildConfig>()

  override val childStack: Value<ChildStack<*, RootComponent.Child>> =
    childStack(
      source = navigation,
      serializer = ChildConfig.serializer(),
      initialConfiguration = ChildConfig.Alarms,
      handleBackButton = true,
      childFactory = ::createChild,
    )

  private fun createChild(
    config: ChildConfig,
    componentContext: ComponentContext
  ): RootComponent.Child =
    when (config) {
      ChildConfig.Alarms -> {
        RootComponent.Child.Alarms(
          DefaultAlarmsComponent(
            componentContext = componentContext,
            onAddAlarmClick = ::onAddAlarmClick,
            onAddGroupClick = ::onAddGroupClick
          )
        )
      }
      is ChildConfig.Alarm -> {
        RootComponent.Child.Alarm(
          DefaultAlarmComponent(componentContext = componentContext, mode = config.mode)
        )
      }
      is ChildConfig.Group -> {
        RootComponent.Child.Group(
          DefaultGroupComponent(componentContext = componentContext, mode = config.mode)
        )
      }
    }

  override fun onBackClick() {
    navigation.pop()
  }

  override fun onAddAlarmClick() {
    navigation.push(ChildConfig.Alarm(AlarmComponent.Mode.Add))
  }

  override fun onEditAlarmClick() {
    navigation.push(ChildConfig.Alarm(AlarmComponent.Mode.Edit))
  }

  override fun onAddGroupClick() {
    navigation.push(ChildConfig.Group(GroupComponent.Mode.Add))
  }

  override fun onEditGroupClick() {
    navigation.push(ChildConfig.Group(GroupComponent.Mode.Edit))
  }

  @Serializable
  private sealed interface ChildConfig {
    @Serializable data object Alarms : ChildConfig

    @Serializable data class Alarm(val mode: AlarmComponent.Mode) : ChildConfig

    @Serializable data class Group(val mode: GroupComponent.Mode) : ChildConfig
  }
}
