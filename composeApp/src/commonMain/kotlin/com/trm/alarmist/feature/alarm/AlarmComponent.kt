package com.trm.alarmist.feature.alarm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.trm.alarmist.feature.alarm.sound.DefaultAlarmSoundDialogComponent
import com.trm.alarmist.feature.alarm.time.DefaultAlarmTimeDialogComponent
import kotlinx.serialization.Serializable

interface AlarmComponent {
  val mode: Mode

  val feature: AlarmFeature

  val dialog: Value<ChildSlot<*, AlarmDialogChild>>

  fun onSoundClick()

  fun onFireAtTimeClick()

  @Serializable
  sealed interface Mode {
    @Serializable data object Add : Mode

    @Serializable data class Edit(val alarmId: Long) : Mode
  }
}

class DefaultAlarmComponent(
  componentContext: ComponentContext,
  override val mode: AlarmComponent.Mode,
) : AlarmComponent, ComponentContext by componentContext {
  override val feature = instanceKeeper.getOrCreate {
    AlarmFeature(
      savedStateContainer =
        stateKeeper.consume(key = SAVED_STATE_KEY, strategy = SerializableContainer.serializer()),
      mode = mode,
    )
  }

  private val dialogNavigation = SlotNavigation<AlarmDialogChildConfig>()

  override val dialog: Value<ChildSlot<*, AlarmDialogChild>> =
    childSlot(
      key = "AlarmDialogSlot",
      source = dialogNavigation,
      serializer = AlarmDialogChildConfig.serializer(),
      handleBackButton = true,
    ) { config, childComponentContext ->
      when (config) {
        AlarmDialogChildConfig.Sound -> {
          AlarmDialogChild.Sound(
            DefaultAlarmSoundDialogComponent(
              componentContext = childComponentContext,
              selectedSoundId = feature.state.value.soundId,
              onSoundSelected = { soundId ->
                feature.onSoundChange(soundId)
                dialogNavigation.dismiss()
              },
              onDismiss = dialogNavigation::dismiss,
            )
          )
        }
        AlarmDialogChildConfig.Time -> {
          AlarmDialogChild.Time(
            DefaultAlarmTimeDialogComponent(
              componentContext = childComponentContext,
              time = requireNotNull(feature.state.value.fireAtTime),
              onConfirm = { time ->
                feature.onFireAtChange(time)
                dialogNavigation.dismiss()
              },
              onDismiss = dialogNavigation::dismiss,
            )
          )
        }
      }
    }

  init {
    stateKeeper.register(
      key = SAVED_STATE_KEY,
      strategy = SerializableContainer.serializer(),
      supplier = feature::saveState,
    )
  }

  override fun onSoundClick() {
    dialogNavigation.activate(AlarmDialogChildConfig.Sound)
  }

  override fun onFireAtTimeClick() {
    dialogNavigation.activate(AlarmDialogChildConfig.Time)
  }

  companion object {
    private const val SAVED_STATE_KEY = "ALARM_STATE"
  }
}
