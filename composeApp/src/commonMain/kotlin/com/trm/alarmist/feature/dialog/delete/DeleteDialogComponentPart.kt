package com.trm.alarmist.feature.dialog.delete

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
import com.arkivanov.decompose.value.Value
import com.trm.alarmist.core.common.util.getStringBlocking
import com.trm.alarmist.feature.alarm.AlarmComponent
import com.trm.alarmist.feature.dialog.DefaultDialogComponent
import com.trm.alarmist.feature.dialog.DialogComponent
import com.trm.alarmist.feature.group.GroupComponent
import com.trm.alarmist.feature.sheet.BottomSheetChild

interface DeleteDialogComponentPart {
  val component: Value<ChildSlot<*, DialogComponent>>

  fun onDelete()
}

class DefaultDeleteDialogComponentPart(
  componentContext: ComponentContext,
  childSlotKey: String,
  private val bottomSheetChild: () -> BottomSheetChild?,
) : DeleteDialogComponentPart, ComponentContext by componentContext {
  private val dialogNavigation = SlotNavigation<DialogComponent.Config>()

  override val component: Value<ChildSlot<*, DialogComponent>> =
    childSlot(
      key = childSlotKey,
      source = dialogNavigation,
      serializer = DialogComponent.Config.serializer(),
      handleBackButton = true,
    ) { config, childComponentContext ->
      DefaultDialogComponent(
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

  override fun onDelete() {
    dialogNavigation.activate(
      DialogComponent.Config(
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
    when (val active = bottomSheetChild()) {
      is BottomSheetChild.Alarm -> {
        if (active.component.mode is AlarmComponent.Mode.Edit) {
          alarmParameter(active.component)
        } else {
          fallback()
        }
      }
      is BottomSheetChild.Group -> {
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
}
