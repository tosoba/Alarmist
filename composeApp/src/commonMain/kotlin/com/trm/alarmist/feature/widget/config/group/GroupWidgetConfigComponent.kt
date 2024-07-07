package com.trm.alarmist.feature.widget.config.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.core.domain.model.AlarmGroupModel
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.alarm.DefaultAlarmComponent
import com.trm.alarmist.feature.group.DefaultGroupComponent
import com.trm.alarmist.feature.group.GroupComponent
import kotlinx.serialization.Serializable

interface GroupWidgetConfigComponent {
  val feature: GroupWidgetConfigFeature

  val bottomSheet: Value<ChildSlot<*, BottomSheetChild>>

  fun onBottomSheetDismissRequest()

  fun onAddGroupClick()

  fun onEditGroupClick(group: AlarmGroupModel)

  sealed interface BottomSheetChild {
    class Alarm(val component: AlarmComponent) : BottomSheetChild

    class Group(val component: GroupComponent) : BottomSheetChild
  }
}

class DefaultGroupWidgetConfigComponent(componentContext: ComponentContext) :
  GroupWidgetConfigComponent, ComponentContext by componentContext {
  override val feature: GroupWidgetConfigFeature =
    instanceKeeper.getOrCreate {
      GroupWidgetConfigFeature(
        stateKeeper.consume(key = SAVED_STATE_KEY, strategy = SerializableContainer.serializer())
      )
    }

  init {
    stateKeeper.register(
      key = SAVED_STATE_KEY,
      strategy = SerializableContainer.serializer(),
      supplier = feature::saveState,
    )
  }

  private val bottomSheetNavigation = SlotNavigation<BottomSheetChildConfig>()

  override val bottomSheet: Value<ChildSlot<*, GroupWidgetConfigComponent.BottomSheetChild>> =
    childSlot(
      key = "GroupWidgetBottomSheetSlot",
      source = bottomSheetNavigation,
      serializer = BottomSheetChildConfig.serializer(),
      handleBackButton = true,
    ) { config, childComponentContext ->
      when (config) {
        is BottomSheetChildConfig.Alarm -> {
          GroupWidgetConfigComponent.BottomSheetChild.Alarm(
            DefaultAlarmComponent(componentContext = childComponentContext, mode = config.mode)
          )
        }
        is BottomSheetChildConfig.Group -> {
          GroupWidgetConfigComponent.BottomSheetChild.Group(
            DefaultGroupComponent(componentContext = childComponentContext, mode = config.mode)
          )
        }
      }
    }

  override fun onBottomSheetDismissRequest() {
    bottomSheetNavigation.dismiss()
  }

  override fun onAddGroupClick() {
    bottomSheetNavigation.activate(BottomSheetChildConfig.Group(GroupComponent.Mode.Add))
  }

  override fun onEditGroupClick(group: AlarmGroupModel) {
    bottomSheetNavigation.activate(BottomSheetChildConfig.Group(GroupComponent.Mode.Edit(group)))
  }

  @Serializable
  private sealed interface BottomSheetChildConfig {
    @Serializable data class Alarm(val mode: AlarmComponent.Mode) : BottomSheetChildConfig

    @Serializable data class Group(val mode: GroupComponent.Mode) : BottomSheetChildConfig
  }

  companion object {
    private const val SAVED_STATE_KEY = "GROUP_WIDGET_CONFIG_STATE"
  }
}
