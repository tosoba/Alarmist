package com.trm.alarmist.feature.alarm.sound

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable

interface AlarmSoundDialogComponent {
  val selectedSoundId: String?
  val onSoundSelected: (String) -> Unit
  val onDismiss: () -> Unit

  @Serializable data object Config
}

class DefaultAlarmSoundDialogComponent(
  private val componentContext: ComponentContext,
  override val selectedSoundId: String?,
  override val onSoundSelected: (String) -> Unit,
  override val onDismiss: () -> Unit,
) : AlarmSoundDialogComponent, ComponentContext by componentContext
