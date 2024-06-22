package com.trm.alarmist.feature.root

import alarmist.composeapp.generated.resources.Res
import alarmist.composeapp.generated.resources.delete_alarm
import alarmist.composeapp.generated.resources.delete_alarm_confirmation_message
import alarmist.composeapp.generated.resources.delete_group
import alarmist.composeapp.generated.resources.delete_group_confirmation_message
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.alarm.DefaultAlarmComponent
import com.trm.alarmist.feature.alarms.AlarmsComponent
import com.trm.alarmist.feature.alarms.DefaultAlarmsComponent
import com.trm.alarmist.feature.group.DefaultGroupComponent
import com.trm.alarmist.feature.group.GroupComponent
import com.trm.alarmist.feature.stopwatch.DefaultStopwatchComponent
import com.trm.alarmist.feature.stopwatch.StopwatchComponent
import com.trm.alarmist.feature.timer.DefaultTimerComponent
import com.trm.alarmist.feature.timer.TimerComponent
import com.trm.alarmist.feature.widgets.DefaultWidgetsComponent
import com.trm.alarmist.feature.widgets.WidgetsComponent
import kotlinx.serialization.Serializable

interface RootComponent : BackHandlerOwner {
  val childStack: Value<ChildStack<*, Child>>

  val dialog: Value<ChildSlot<*, RootDialogComponent>>

  val bottomSheet: Value<ChildSlot<*, BottomSheetChild>>

  fun onAlarmsDrawerItemClick()

  fun onWidgetsDrawerItemClick()

  fun onTimerDrawerItemClick()

  fun onStopwatchDrawerItemClick()

  fun onDeleteActionClick()

  fun onAddAlarmClick()

  fun onEditAlarmClick(alarm: AlarmListModel)

  fun onEditUpcomingAlarmClick(alarm: UpcomingAlarmListModel)

  fun onAddGroupClick()

  fun onEditGroupClick(group: AlarmGroupModel)

  fun onBackClick()

  fun onBottomSheetDismissRequest()

  sealed interface BottomSheetChild {
    class Alarm(val component: AlarmComponent) : BottomSheetChild

    class Group(val component: GroupComponent) : BottomSheetChild
  }

  sealed interface Child {
    class Alarms(val component: AlarmsComponent) : Child

    class Widgets(val component: WidgetsComponent) : Child

    class Timer(val component: TimerComponent) : Child

    class Stopwatch(val component: StopwatchComponent) : Child
  }
}

class DefaultRootComponent(componentContext: ComponentContext, startMode: RootStartMode) :
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
      key = "DialogSlot",
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
            alarmParameter = { it.feature::onDeleteClick },
            groupParameter = { it.feature::onDeleteClick },
          )()
        },
        onDismiss = dialogNavigation::dismiss,
      )
    }

  private val bottomSheetNavigation = SlotNavigation<BottomSheetChildConfig>()

  override val bottomSheet: Value<ChildSlot<*, RootComponent.BottomSheetChild>> =
    childSlot(
      key = "BottomSheetSlot",
      source = bottomSheetNavigation,
      serializer = BottomSheetChildConfig.serializer(),
      initialConfiguration = {
        when (startMode) {
          RootStartMode.AddAlarm -> BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Add)
          is RootStartMode.EditAlarm ->
            TODO() // BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Edit(startMode.id))
          RootStartMode.Normal -> null
        }
      },
      handleBackButton = true,
    ) { config, childComponentContext ->
      when (config) {
        is BottomSheetChildConfig.Alarm -> {
          RootComponent.BottomSheetChild.Alarm(
            DefaultAlarmComponent(componentContext = childComponentContext, mode = config.mode)
          )
        }
        is BottomSheetChildConfig.Group -> {
          RootComponent.BottomSheetChild.Group(
            DefaultGroupComponent(componentContext = childComponentContext, mode = config.mode)
          )
        }
      }
    }

  init {
    instanceKeeper.getOrCreate(::RootFeature)
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
            onEditUpcomingAlarmClick = ::onEditUpcomingAlarmClick,
            onAddGroupClick = ::onAddGroupClick,
            onEditGroupClick = ::onEditGroupClick,
          )
        )
      }
      ChildConfig.Widgets -> {
        RootComponent.Child.Widgets(DefaultWidgetsComponent(componentContext))
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

  override fun onWidgetsDrawerItemClick() {
    navigation.replaceAll(ChildConfig.Widgets)
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
            alarmParameter = { getStringBlocking(Res.string.delete_alarm) },
            groupParameter = { getStringBlocking(Res.string.delete_group) },
          ),
        message =
          deleteActionParameter(
            alarmParameter = { getStringBlocking(Res.string.delete_alarm_confirmation_message) },
            groupParameter = { getStringBlocking(Res.string.delete_group_confirmation_message) },
          ),
      )
    )
  }

  private fun <T> deleteActionParameter(
    alarmParameter: (AlarmComponent) -> T,
    groupParameter: (GroupComponent) -> T,
    fallback: () -> T = { throw IllegalStateException() },
  ): T =
    when (val active = bottomSheet.value.child?.instance) {
      is RootComponent.BottomSheetChild.Alarm -> {
        if (active.component.mode is AlarmComponent.Mode.Edit) {
          alarmParameter(active.component)
        } else {
          fallback()
        }
      }
      is RootComponent.BottomSheetChild.Group -> {
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
    bottomSheetNavigation.activate(BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Add))
  }

  override fun onEditAlarmClick(alarm: AlarmListModel) {
    bottomSheetNavigation.activate(BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Edit(alarm.id)))
  }

  override fun onEditUpcomingAlarmClick(alarm: UpcomingAlarmListModel) {
    bottomSheetNavigation.activate(BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Edit(alarm.id)))
  }

  override fun onAddGroupClick() {
    bottomSheetNavigation.activate(BottomSheetChildConfig.Group(GroupComponent.Mode.Add))
  }

  override fun onEditGroupClick(group: AlarmGroupModel) {
    bottomSheetNavigation.activate(BottomSheetChildConfig.Group(GroupComponent.Mode.Edit(group)))
  }

  override fun onBackClick() {
    navigation.pop()
  }

  override fun onBottomSheetDismissRequest() {
    bottomSheetNavigation.dismiss()
  }

  @Serializable
  private sealed interface BottomSheetChildConfig {
    @Serializable data class Alarm(val mode: AlarmComponent.Mode) : BottomSheetChildConfig

    @Serializable data class Group(val mode: GroupComponent.Mode) : BottomSheetChildConfig
  }

  @Serializable
  private sealed interface ChildConfig {
    @Serializable data object Alarms : ChildConfig

    @Serializable data object Widgets : ChildConfig

    @Serializable data object Timer : ChildConfig

    @Serializable data object Stopwatch : ChildConfig
  }
}
