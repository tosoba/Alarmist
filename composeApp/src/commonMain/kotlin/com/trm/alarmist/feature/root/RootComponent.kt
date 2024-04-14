package com.trm.alarmist.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
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

  val dialog: Value<ChildSlot<*, RootDialogComponent>>

  fun onAlarmsDrawerItemClick()

  fun onClockDrawerItemClick()

  fun onTimerDrawerItemClick()

  fun onStopwatchDrawerItemClick()

  fun onDeleteActionClick()

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

  private val dialogNavigation = SlotNavigation<RootDialogComponent.Config>()

  override val dialog: Value<ChildSlot<*, RootDialogComponent>> =
    childSlot(
      source = dialogNavigation,
      serializer = RootDialogComponent.Config.serializer(),
      handleBackButton = true,
    ) { config, childComponentContext ->
      DefaultRootDialogComponent(
        componentContext = childComponentContext,
        title = config.title,
        message = config.message,
        onConfirm = {
          dialogNavigation.dismiss()
          deleteActionParameter(
            alarmParameter = { it::onDeleteActionClick },
            groupParameter = { it::onDeleteActionClick },
          )()
        },
        onDismiss = dialogNavigation::dismiss,
      )
    }

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
            onEditGroupClick = ::onEditGroupClick,
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

  override fun onDeleteActionClick() {
    dialogNavigation.activate(
      RootDialogComponent.Config(
        title =
          deleteActionParameter(
            alarmParameter = { "Delete alarm" },
            groupParameter = { "Delete group" },
          ),
        message =
          deleteActionParameter(
            alarmParameter = { "Are you sure you want to delete this alarm?" },
            groupParameter = { "Are you sure you want to delete this group?" },
          ),
      )
    )
  }

  private fun <T> deleteActionParameter(
    alarmParameter: (AlarmComponent) -> T,
    groupParameter: (GroupComponent) -> T,
    fallback: () -> T = { throw IllegalStateException() },
  ) =
    when (val active = childStack.active.instance) {
      is RootComponent.Child.Alarm -> {
        if (active.component.mode is AlarmComponent.Mode.Edit) {
          alarmParameter(active.component)
        } else {
          fallback()
        }
      }
      is RootComponent.Child.Group -> {
        if (active.component.mode is GroupComponent.Mode.Edit) {
          groupParameter(active.component)
        } else {
          fallback()
        }
      }
      else -> {
        fallback()
      }
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
