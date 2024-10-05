package com.trm.alarmist.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.core.domain.model.AlarmListModel
import com.trm.alarmist.core.domain.model.UpcomingAlarmListModel
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.alarm.DefaultAlarmComponent
import com.trm.alarmist.feature.alarms.AlarmsComponent
import com.trm.alarmist.feature.alarms.DefaultAlarmsComponent
import com.trm.alarmist.feature.dialog.delete.DefaultDeleteDialogComponentPart
import com.trm.alarmist.feature.dialog.delete.DeleteDialogComponentPart
import com.trm.alarmist.feature.group.DefaultGroupComponent
import com.trm.alarmist.feature.group.GroupComponent
import com.trm.alarmist.feature.sheet.BottomSheetChild
import com.trm.alarmist.feature.sheet.BottomSheetChildConfig
import com.trm.alarmist.feature.stopwatch.DefaultStopwatchComponent
import com.trm.alarmist.feature.stopwatch.StopwatchComponent
import com.trm.alarmist.feature.timer.DefaultTimerComponent
import com.trm.alarmist.feature.timer.TimerComponent
import com.trm.alarmist.feature.widgets.DefaultWidgetsComponent
import com.trm.alarmist.feature.widgets.WidgetsComponent
import kotlinx.serialization.Serializable

interface RootComponent : BackHandlerOwner {
  val childStack: Value<ChildStack<*, Child>>

  val deleteDialog: DeleteDialogComponentPart

  val bottomSheet: Value<ChildSlot<*, BottomSheetChild>>

  fun onAlarmsDrawerItemClick()

  fun onWidgetsDrawerItemClick()

  fun onTimerDrawerItemClick()

  fun onStopwatchDrawerItemClick()

  fun onAddAlarmClick()

  fun onEditAlarmClick(alarm: AlarmListModel)

  fun onEditUpcomingAlarmClick(alarm: UpcomingAlarmListModel)

  fun onAddGroupClick()

  fun onEditGroupClick(group: AlarmGroupModel)

  fun onBackClick()

  fun onBottomSheetDismissRequest()

  fun onStartModeChanged(startMode: RootStartMode?)

  sealed interface Child {
    class Alarms(val component: AlarmsComponent) : Child

    class Widgets(val component: WidgetsComponent) : Child

    class Timer(val component: TimerComponent) : Child

    class Stopwatch(val component: StopwatchComponent) : Child
  }
}

class DefaultRootComponent(componentContext: ComponentContext, startMode: RootStartMode) :
  RootComponent, ComponentContext by componentContext {
  constructor(componentContext: ComponentContext) : this(componentContext, RootStartMode.Normal)

  private val navigation = StackNavigation<ChildConfig>()

  override val childStack: Value<ChildStack<*, RootComponent.Child>> =
    childStack(
      source = navigation,
      serializer = ChildConfig.serializer(),
      initialStack = {
        when (startMode) {
          RootStartMode.Timer -> listOf(ChildConfig.Alarms, ChildConfig.Timer)
          RootStartMode.Stopwatch -> listOf(ChildConfig.Alarms, ChildConfig.Stopwatch)
          else -> listOf(ChildConfig.Alarms)
        }
      },
      childFactory = ::createChild,
    )

  private val bottomSheetNavigation = SlotNavigation<BottomSheetChildConfig>()

  override val bottomSheet: Value<ChildSlot<*, BottomSheetChild>> =
    childSlot(
      key = "RootBottomSheetSlot",
      source = bottomSheetNavigation,
      serializer = BottomSheetChildConfig.serializer(),
      initialConfiguration = {
        when (startMode) {
          RootStartMode.AddAlarm -> {
            BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Add)
          }
          is RootStartMode.EditAlarm -> {
            BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Edit(startMode.id))
          }
          else -> {
            null
          }
        }
      },
      handleBackButton = true,
    ) { config, childComponentContext ->
      when (config) {
        is BottomSheetChildConfig.Alarm -> {
          BottomSheetChild.Alarm(
            DefaultAlarmComponent(componentContext = childComponentContext, mode = config.mode)
          )
        }
        is BottomSheetChildConfig.Group -> {
          BottomSheetChild.Group(
            DefaultGroupComponent(componentContext = childComponentContext, mode = config.mode)
          )
        }
      }
    }

  override val deleteDialog: DeleteDialogComponentPart =
    DefaultDeleteDialogComponentPart(
      componentContext = componentContext,
      childSlotKey = "RootDialogSlot",
    ) {
      bottomSheet.value.child?.instance
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
    goToAlarms()
  }

  private fun goToAlarms() {
    navigation.bringToFront(ChildConfig.Alarms)
  }

  override fun onWidgetsDrawerItemClick() {
    navigation.bringToFront(ChildConfig.Widgets)
  }

  override fun onTimerDrawerItemClick() {
    goToTimer()
  }

  private fun goToTimer() {
    navigation.bringToFront(ChildConfig.Timer)
  }

  override fun onStopwatchDrawerItemClick() {
    goToStopwatch()
  }

  private fun goToStopwatch() {
    navigation.bringToFront(ChildConfig.Stopwatch)
  }

  override fun onAddAlarmClick() {
    goToAddAlarm()
  }

  private fun goToAddAlarm() {
    bottomSheetNavigation.activate(BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Add))
  }

  override fun onEditAlarmClick(alarm: AlarmListModel) {
    goToEditAlarm(alarm.id)
  }

  private fun goToEditAlarm(alarmId: Long) {
    bottomSheetNavigation.activate(BottomSheetChildConfig.Alarm(AlarmComponent.Mode.Edit(alarmId)))
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

  override fun onStartModeChanged(startMode: RootStartMode?) {
    when (startMode) {
      RootStartMode.AddAlarm -> {
        goToAlarms()
        goToAddAlarm()
      }
      is RootStartMode.EditAlarm -> {
        goToAlarms()
        goToEditAlarm(startMode.id)
      }
      RootStartMode.Stopwatch -> {
        goToStopwatch()
      }
      RootStartMode.Timer -> {
        goToTimer()
      }
      RootStartMode.Normal -> {
        goToAlarms()
      }
      null -> {
        return
      }
    }
  }

  @Serializable
  private sealed interface ChildConfig {
    @Serializable data object Alarms : ChildConfig

    @Serializable data object Widgets : ChildConfig

    @Serializable data object Timer : ChildConfig

    @Serializable data object Stopwatch : ChildConfig
  }
}
