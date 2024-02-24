package com.trm.alarmist.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.alarm.DefaultAlarmComponent
import com.trm.alarmist.feature.alarms.AlarmsComponent
import com.trm.alarmist.feature.alarms.DefaultAlarmsComponent
import com.trm.alarmist.feature.clock.ClockComponent
import com.trm.alarmist.feature.clock.DefaultClockComponent
import com.trm.alarmist.feature.group.DefaultGroupComponent
import com.trm.alarmist.feature.group.GroupComponent
import com.trm.alarmist.feature.stopwatch.DefaultStopwatchComponent
import com.trm.alarmist.feature.stopwatch.StopwatchComponent
import com.trm.alarmist.feature.timer.DefaultTimerComponent
import com.trm.alarmist.feature.timer.TimerComponent
import kotlinx.serialization.Serializable

interface RootComponent {
  val childStack: Value<ChildStack<*, Child>>

  fun onAlarmsDrawerItemClick()

  fun onClockDrawerItemClick()

  fun onTimerDrawerItemClick()

  fun onStopwatchDrawerItemClick()

  fun onAddAlarmClick()

  fun onEditAlarmClick(alarm: AlarmListModel)

  fun onAddGroupClick()

  fun onEditGroupClick(group: AlarmGroupModel)

  fun onBackClick()

  sealed interface Child {
    class Alarms(val component: AlarmsComponent) : Child

    class Alarm(val component: AlarmComponent) : Child

    class Group(val component: GroupComponent) : Child

    class Clock(val component: ClockComponent) : Child

    class Timer(val component: TimerComponent) : Child

    class Stopwatch(val component: StopwatchComponent) : Child
  }
}

class DefaultRootComponent(componentContext: ComponentContext) :
  RootComponent, ComponentContext by componentContext {
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
    componentContext: ComponentContext,
  ): RootComponent.Child =
    when (config) {
      ChildConfig.Alarms -> {
        RootComponent.Child.Alarms(
          DefaultAlarmsComponent(
            componentContext = componentContext,
            onAddAlarmClick = ::onAddAlarmClick,
            onEditAlarmClick = ::onEditAlarmClick,
            onAddGroupClick = ::onAddGroupClick,
          )
        )
      }
      is ChildConfig.Alarm -> {
        RootComponent.Child.Alarm(
          DefaultAlarmComponent(
            componentContext = componentContext,
            mode = config.mode,
            pop = ::onBackClick,
          )
        )
      }
      is ChildConfig.Group -> {
        RootComponent.Child.Group(
          DefaultGroupComponent(
            componentContext = componentContext,
            mode = config.mode,
            pop = ::onBackClick,
          )
        )
      }
      ChildConfig.Clock -> {
        RootComponent.Child.Clock(DefaultClockComponent(componentContext))
      }
      ChildConfig.Timer -> {
        RootComponent.Child.Timer(DefaultTimerComponent(componentContext))
      }
      ChildConfig.Stopwatch -> {
        RootComponent.Child.Stopwatch(DefaultStopwatchComponent(componentContext))
      }
    }

  override fun onAlarmsDrawerItemClick() {
    navigation.replaceAll(ChildConfig.Alarms)
  }

  override fun onClockDrawerItemClick() {
    navigation.replaceAll(ChildConfig.Clock)
  }

  override fun onTimerDrawerItemClick() {
    navigation.replaceAll(ChildConfig.Timer)
  }

  override fun onStopwatchDrawerItemClick() {
    navigation.replaceAll(ChildConfig.Stopwatch)
  }

  override fun onAddAlarmClick() {
    navigation.push(ChildConfig.Alarm(AlarmComponent.Mode.Add))
  }

  override fun onEditAlarmClick(alarm: AlarmListModel) {
    navigation.push(ChildConfig.Alarm(AlarmComponent.Mode.Edit(alarm)))
  }

  override fun onAddGroupClick() {
    navigation.push(ChildConfig.Group(GroupComponent.Mode.Add))
  }

  override fun onEditGroupClick(group: AlarmGroupModel) {
    navigation.push(ChildConfig.Group(GroupComponent.Mode.Edit(group)))
  }

  override fun onBackClick() {
    navigation.pop()
  }

  @Serializable
  private sealed interface ChildConfig {
    @Serializable data object Alarms : ChildConfig

    @Serializable data class Alarm(val mode: AlarmComponent.Mode) : ChildConfig

    @Serializable data class Group(val mode: GroupComponent.Mode) : ChildConfig

    @Serializable data object Clock : ChildConfig

    @Serializable data object Timer : ChildConfig

    @Serializable data object Stopwatch : ChildConfig
  }
}
